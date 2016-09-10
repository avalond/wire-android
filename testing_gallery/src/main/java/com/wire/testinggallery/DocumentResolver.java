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
package com.wire.testinggallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class DocumentResolver {

    private static final String[] ID_COLUMNS = new String[] {
        MediaStore.Files.FileColumns._ID
    };
    private final ContentResolver contentResolver;

    public DocumentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Uri getDocumentPath() {
        return query(MediaStore.Files.getContentUri("external"), ID_COLUMNS);
    }

    public Uri getVideoPath() {
        return query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null);
    }

    public Uri getImagePath() {
        return query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
    }

    private Uri query(Uri baseUri, String[] projection) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(baseUri, projection, null, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

            final int columnFileIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            if (cursor.moveToNext()) {
                final int id = cursor.getInt(columnFileIdIndex);
                return Uri.withAppendedPath(baseUri, String.valueOf(id));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
}
