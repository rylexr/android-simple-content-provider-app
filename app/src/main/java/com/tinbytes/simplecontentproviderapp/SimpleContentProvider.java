/*
 * Copyright 2015 Tinbytes Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tinbytes.simplecontentproviderapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class SimpleContentProvider extends ContentProvider {
  // Provide a mechanism to identify all the incoming uri patterns.
  private static final int NOTES = 1;
  private static final int LABELS = 2;
  private static final int NOTE_ID_LABELS = 3;
  private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    // /note
    uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.NoteTable.URI_PATH, NOTES);
    // /label
    uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.LabelTable.URI_PATH, LABELS);
    // /note/{note_id}/labels
    uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.NoteTable.URI_PATH + "/#/" + DatabaseContract.LabelTable.URI_PATH, NOTE_ID_LABELS);
  }

  private DatabaseHelper dh;

  public boolean onCreate() {
    dh = new DatabaseHelper(getContext());
    return true;
  }

  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    Cursor c;
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setDistinct(true);
    switch (uriMatcher.match(uri)) {
      case NOTES:
        qb.setTables(DatabaseContract.NoteTable.TABLE_NAME);
        if (sortOrder == null)
          sortOrder = DatabaseContract.NoteTable.CREATED_ON + " ASC";
        c = qb.query(dh.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case LABELS:
        qb.setTables(DatabaseContract.LabelTable.TABLE_NAME);
        if (sortOrder == null)
          sortOrder = DatabaseContract.LabelTable.TEXT + " ASC";
        c = qb.query(dh.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case NOTE_ID_LABELS:
        qb.setTables(DatabaseContract.LabelTable.TABLE_NAME + " l");
        sortOrder = "l." + DatabaseContract.LabelTable.TEXT + " ASC";
        qb.appendWhere("EXISTS(SELECT nl." + DatabaseContract.NoteLabelTable.NOTE_ID + " FROM " +
            DatabaseContract.NoteLabelTable.TABLE_NAME + " nl WHERE " +
            "nl." + DatabaseContract.NoteLabelTable.NOTE_ID + "=" + uri.getPathSegments().get(1) + " AND " +
            "nl." + DatabaseContract.NoteLabelTable.LABEL_ID + " = l." + DatabaseContract.LabelTable.ID + ")");
        c = qb.query(dh.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    if (getContext() != null) {
      c.setNotificationUri(getContext().getContentResolver(), uri);
    }
    return c;
  }

  private Uri doInsert(Uri uri, String tableName, Uri contentUri, ContentValues contentValues) {
    SQLiteDatabase db = dh.getWritableDatabase();
    long rowId = db.insert(tableName, null, contentValues);
    if (rowId > 0) {
      Uri insertedUri = ContentUris.withAppendedId(contentUri, rowId);
      if (getContext() != null) {
        getContext().getContentResolver().notifyChange(insertedUri, null);
      }
      return insertedUri;
    }
    throw new SQLException("Failed to insert row - " + uri);
  }

  public Uri insert(@NonNull Uri uri, ContentValues values) {
    if (values != null) {
      switch (uriMatcher.match(uri)) {
        case NOTES:
          return doInsert(uri, DatabaseContract.NoteTable.TABLE_NAME, DatabaseContract.NoteTable.CONTENT_URI, values);
        case LABELS:
          return doInsert(uri, DatabaseContract.LabelTable.TABLE_NAME, DatabaseContract.LabelTable.CONTENT_URI, values);
        case NOTE_ID_LABELS:
          return doInsert(uri, DatabaseContract.NoteLabelTable.TABLE_NAME, DatabaseContract.NoteLabelTable.CONTENT_URI, values);
        default:
          throw new IllegalArgumentException("Unknown URI " + uri);
      }
    }
    return null;
  }

  public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    SQLiteDatabase db = dh.getWritableDatabase();
    int count;
    switch (uriMatcher.match(uri)) {
      case NOTES:
        count = db.update(DatabaseContract.NoteTable.TABLE_NAME, values, selection, selectionArgs);
        break;
      case LABELS:
        count = db.update(DatabaseContract.LabelTable.TABLE_NAME, values, selection, selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    if (getContext() != null) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return count;
  }

  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = dh.getWritableDatabase();
    int count;
    switch (uriMatcher.match(uri)) {
      case NOTES:
        StringBuilder in = new StringBuilder("(");
        Cursor c = db.query(DatabaseContract.NoteTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        boolean moveToNext = c.moveToNext();
        while (moveToNext) {
          in.append(c.getInt(c.getColumnIndex(DatabaseContract.NoteTable.ID)));
          moveToNext = c.moveToNext();
          if (moveToNext) {
            in.append(",");
          }
        }
        in.append(")");
        c.close();
        count = db.delete(DatabaseContract.NoteTable.TABLE_NAME, selection, selectionArgs);
        if (count > 0) {
          count = db.delete(DatabaseContract.NoteLabelTable.TABLE_NAME, DatabaseContract.NoteLabelTable.NOTE_ID + " IN " + in, null);
        }
        break;
      case LABELS:
        in = new StringBuilder("(");
        c = db.query(DatabaseContract.LabelTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        moveToNext = c.moveToNext();
        while (moveToNext) {
          in.append(c.getInt(c.getColumnIndex(DatabaseContract.LabelTable.ID)));
          moveToNext = c.moveToNext();
          if (moveToNext) {
            in.append(",");
          }
        }
        in.append(")");
        c.close();
        count = db.delete(DatabaseContract.LabelTable.TABLE_NAME, selection, selectionArgs);
        if (count > 0) {
          count = db.delete(DatabaseContract.NoteLabelTable.TABLE_NAME, DatabaseContract.NoteLabelTable.LABEL_ID + " IN " + in, null);
        }
        break;
      case NOTE_ID_LABELS:
        count = db.delete(DatabaseContract.NoteLabelTable.TABLE_NAME,
            DatabaseContract.NoteLabelTable.NOTE_ID + "=" + uri.getPathSegments().get(1) +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    if (getContext() != null) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return count;
  }

  public String getType(@NonNull Uri uri) {
    switch (uriMatcher.match(uri)) {
      case NOTES:
        return DatabaseContract.NoteTable.CONTENT_TYPE;
      case LABELS:
        return DatabaseContract.LabelTable.CONTENT_TYPE;
      case NOTE_ID_LABELS:
        return DatabaseContract.LabelTable.CONTENT_LABEL_TYPE;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
  }
}
