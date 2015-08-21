/*
 * Copyright 2015 tinbytes.com
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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Date;

public class SimpleContentProviderActivity extends AppCompatActivity {
  private static final String TAG = SimpleContentProviderActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_content_provider);

    findViewById(R.id.bInsertData).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        insertData();
      }
    });
    findViewById(R.id.bListData).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listData();
      }
    });
    findViewById(R.id.bUpdateData).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateData();
      }
    });
    findViewById(R.id.bDeleteData).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteData();
      }
    });
  }

  private void insertData() {
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.NoteTable.TEXT, "Note 1");
    values.put(DatabaseContract.NoteTable.CREATED_ON, new Date().getTime());
    long noteId1 = ContentUris.parseId(getContentResolver().insert(DatabaseContract.NoteTable.CONTENT_URI, values));

    values.put(DatabaseContract.NoteTable.TEXT, "Note 2");
    values.put(DatabaseContract.NoteTable.CREATED_ON, new Date().getTime());
    long noteId2 = ContentUris.parseId(getContentResolver().insert(DatabaseContract.NoteTable.CONTENT_URI, values));

    values = new ContentValues();
    values.put(DatabaseContract.LabelTable.TEXT, "Label 1");
    long labelId1 = ContentUris.parseId(getContentResolver().insert(DatabaseContract.LabelTable.CONTENT_URI, values));

    values.put(DatabaseContract.LabelTable.TEXT, "Label 2");
    long labelId2 = ContentUris.parseId(getContentResolver().insert(DatabaseContract.LabelTable.CONTENT_URI, values));

    values.put(DatabaseContract.LabelTable.TEXT, "Label 3");
    long labelId3 = ContentUris.parseId(getContentResolver().insert(DatabaseContract.LabelTable.CONTENT_URI, values));

    values.put(DatabaseContract.LabelTable.TEXT, "Label 4");
    ContentUris.parseId(getContentResolver().insert(DatabaseContract.LabelTable.CONTENT_URI, values));

    values = new ContentValues();
    values.put(DatabaseContract.NoteLabelTable.NOTE_ID, String.valueOf(noteId1));
    values.put(DatabaseContract.NoteLabelTable.LABEL_ID, String.valueOf(labelId1));
    getContentResolver().insert(Uri.parse(DatabaseContract.NoteLabelTable.CONTENT_URI.toString().replace("#", String.valueOf(noteId1))), values);

    values.put(DatabaseContract.NoteLabelTable.NOTE_ID, String.valueOf(noteId1));
    values.put(DatabaseContract.NoteLabelTable.LABEL_ID, String.valueOf(labelId2));
    getContentResolver().insert(Uri.parse(DatabaseContract.NoteLabelTable.CONTENT_URI.toString().replace("#", String.valueOf(noteId1))), values);

    values.put(DatabaseContract.NoteLabelTable.NOTE_ID, String.valueOf(noteId2));
    values.put(DatabaseContract.NoteLabelTable.LABEL_ID, String.valueOf(labelId2));
    getContentResolver().insert(Uri.parse(DatabaseContract.NoteLabelTable.CONTENT_URI.toString().replace("#", String.valueOf(noteId2))), values);

    values.put(DatabaseContract.NoteLabelTable.NOTE_ID, String.valueOf(noteId2));
    values.put(DatabaseContract.NoteLabelTable.LABEL_ID, String.valueOf(labelId3));
    getContentResolver().insert(Uri.parse(DatabaseContract.NoteLabelTable.CONTENT_URI.toString().replace("#", String.valueOf(noteId2))), values);

    Log.d(TAG, "---DATA INSERTED---");
  }

  private void listData() {
    Log.d(TAG, "---NOTES---");
    Cursor c = getContentResolver().query(DatabaseContract.NoteTable.CONTENT_URI, null, null, null, null);
    if (c != null) {
      while (c.moveToNext()) {
        int id = c.getInt(c.getColumnIndex(DatabaseContract.NoteTable.ID));
        Log.d(TAG, "Id: " + id);
        Log.d(TAG, "Note: " + c.getString(c.getColumnIndex(DatabaseContract.NoteTable.TEXT)));
        Log.d(TAG, "Created On: " + new Date(c.getLong(c.getColumnIndex(DatabaseContract.NoteTable.CREATED_ON))));
        Cursor c2 = getContentResolver().query(Uri.parse(DatabaseContract.NoteTable.CONTENT_URI + "/" + id + "/" + DatabaseContract.LabelTable.URI_PATH), null, null, null, null);
        if (c2 != null) {
          Log.d(TAG, "  ---NOTE LABELS---");
          while (c2.moveToNext()) {
            Log.d(TAG, "  -Id: " + c2.getInt(c2.getColumnIndex(DatabaseContract.LabelTable.ID)));
            Log.d(TAG, "  -Label: " + c2.getString(c2.getColumnIndex(DatabaseContract.LabelTable.TEXT)));
          }
          c2.close();
        }
      }
      c.close();
    }

    Log.d(TAG, "---LABELS---");
    c = getContentResolver().query(DatabaseContract.LabelTable.CONTENT_URI, null, null, null, null);
    if (c != null) {
      while (c.moveToNext()) {
        Log.d(TAG, "Id: " + c.getInt(c.getColumnIndex(DatabaseContract.LabelTable.ID)));
        Log.d(TAG, "Label: " + c.getString(c.getColumnIndex(DatabaseContract.LabelTable.TEXT)));
      }
      c.close();
    }
  }

  private void updateData() {
    ContentValues values = new ContentValues();
    values.put(DatabaseContract.NoteTable.TEXT, "Note 1.1");
    getContentResolver().update(DatabaseContract.NoteTable.CONTENT_URI, values, DatabaseContract.NoteTable.TEXT + "=?", new String[]{"Note 1"});
    Log.d(TAG, "---DATA UPDATED---");
  }

  private void deleteData() {
    getContentResolver().delete(DatabaseContract.LabelTable.CONTENT_URI, DatabaseContract.LabelTable.TEXT + "=?", new String[]{"Label 2"});
    Log.d(TAG, "---DATA DELETED---");
  }
}


