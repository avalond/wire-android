/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.core.api.scala;

import android.text.TextUtils;
import com.waz.api.Contacts;
import com.waz.api.ConversationSearchResult;
import com.waz.api.UpdateListener;
import com.waz.api.User;
import com.waz.api.UserSearchResult;
import com.waz.api.ZMessagingApi;
import com.waz.zclient.core.stores.pickuser.PickUserStore;
import com.waz.zclient.core.stores.pickuser.PickUserStoreObserver;

public class ScalaPickUserStore extends PickUserStore {
    public static final String TAG = ScalaPickUserStore.class.getName();

    private ZMessagingApi zMessagingApi;

    private UserSearchResult connectionsResults;
    private UserSearchResult recommendedResults;
    private ConversationSearchResult groupResults;

    private UserSearchResult topResults;
    private Contacts contacts;
    private Contacts searchContacts;

    private String searchFilter = null;

    private String[] excludedUserIds = new String[0];  // List of user id's to exclude from search
    private static final String[] NO_EXCLUDES = new String[0];

    public ScalaPickUserStore(ZMessagingApi zMessagingApi) {
        this.zMessagingApi = zMessagingApi;
    }

    @Override
    public void removePickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver) {
        super.removePickUserStoreObserver(pickUserStoreObserver);
        if (pickUserStoreObservers.isEmpty()) {
            tearDownSearch();
        }
    }

    @Override
    public void tearDown() {
        tearDownSearch();

        if (contacts != null) {
            contacts.removeUpdateListener(contactsUpdateListener);
            contacts = null;
        }

        if (searchContacts != null) {
            searchContacts.removeUpdateListener(searchContactsUpdateListener);
            searchContacts = null;
        }

        zMessagingApi = null;
    }

    @Override
    public void loadTopUserList(int numberOfResults, boolean excludeUsers) {
        searchFilter = null;
        if (topResults != null) {
            topResults.removeUpdateListener(topResultsListener);
        }
        topResults = zMessagingApi.search().getTopPeople(numberOfResults, excludeUsers ? excludedUserIds : NO_EXCLUDES);
        topResults.addUpdateListener(topResultsListener);
    }

    @Override
    public void loadSearchByFilter(String searchTerm, int numberOfResults, boolean excludeUsers) {
        searchFilter = searchTerm;
        String[] excludes = excludeUsers ? excludedUserIds : NO_EXCLUDES;

        if (connectionsResults != null) {
            connectionsResults.removeUpdateListener(searchListener);
        }
        connectionsResults = zMessagingApi.search().getConnectionsByNameOrEmailIncludingBlocked(searchTerm, numberOfResults, excludes);
        connectionsResults.addUpdateListener(searchListener);

        if (recommendedResults != null) {
            recommendedResults.removeUpdateListener(searchListener);
        }
        recommendedResults = zMessagingApi.search().getRecommendedPeople(searchTerm, numberOfResults, excludes);
        recommendedResults.addUpdateListener(searchListener);

        if (groupResults != null) {
            groupResults.removeUpdateListener(searchListener);
        }
        groupResults = zMessagingApi.search().getGroupConversations(searchTerm, numberOfResults);
        groupResults.addUpdateListener(searchListener);
    }

    @Override
    public void loadContacts() {
        if (searchContacts != null) {
            searchContacts.removeUpdateListener(searchContactsUpdateListener);
            searchContacts = null;
        }

        if (contacts == null) {
            contacts = zMessagingApi.getContacts();
            contacts.addUpdateListener(contactsUpdateListener);
        }
        notifyContactsUpdated(contacts);
    }

    @Override
    public void searchContacts(String query) {
        if (contacts != null) {
            contacts.removeUpdateListener(contactsUpdateListener);
            contacts = null;
        }

        if (searchContacts == null) {
            searchContacts = zMessagingApi.search().getContacts(query);
            searchContacts.addUpdateListener(searchContactsUpdateListener);
        } else {
            searchContacts.search(query);
        }
        notifySearchContactsUpdated(searchContacts);
    }

    @Override
    public void resetContactSearch() {
        searchContacts("");
    }

    @Override
    public void setExcludedUsers(String[] users) {
        excludedUserIds = users;
    }

    @Override
    public String[] getExcludedUsers() {
        return excludedUserIds;
    }

    @Override
    public boolean hasTopUsers() {
        return (topResults != null) && (topResults.getAll().length > 0);
    }

    @Override
    public User getUser(String userId) {
        return zMessagingApi.getUser(userId);
    }

    @Override
    public Contacts getContacts() {
        return contacts;
    }

    private void tearDownSearch() {
        if (connectionsResults != null) {
            connectionsResults.removeUpdateListener(searchListener);
        }
        if (recommendedResults != null) {
            recommendedResults.removeUpdateListener(searchListener);
        }
        if (groupResults != null) {
            groupResults.removeUpdateListener(searchListener);
        }
        if (topResults != null) {
            topResults.removeUpdateListener(topResultsListener);
        }
        connectionsResults = null;
        recommendedResults = null;
        groupResults = null;
        topResults = null;
    }

    final private UpdateListener searchListener = new UpdateListener() {
        @Override
        public void updated() {
            if (!TextUtils.isEmpty(searchFilter)) {
                notifySearchResultsUpdated(connectionsResults.getAll(), recommendedResults.getAll(), groupResults.getAll());
            }
        }
    };

    final private UpdateListener topResultsListener = new UpdateListener() {
        @Override
        public void updated() {
            notifyTopUsersUpdated(topResults.getAll());
        }
    };

    final private UpdateListener contactsUpdateListener = new UpdateListener() {
        @Override
        public void updated() {
            if (contacts == null) {
                return;
            }
            notifyContactsUpdated(contacts);
        }
    };

    final private UpdateListener searchContactsUpdateListener = new UpdateListener() {
        @Override
        public void updated() {
            if (searchContacts == null) {
                return;
            }
            notifySearchContactsUpdated(searchContacts);
        }
    };
}
