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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String TAG = DatabaseHelper.class.getSimpleName();

  public DatabaseHelper(Context context) {
    super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Note
    db.execSQL("CREATE TABLE " + DatabaseContract.NoteTable.TABLE_NAME + " (" +
        DatabaseContract.NoteTable.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        DatabaseContract.NoteTable.TEXT + " TEXT NOT NULL," +
        DatabaseContract.NoteTable.CREATED_ON + " INTEGER NOT NULL)");
    // Label
    db.execSQL("CREATE TABLE " + DatabaseContract.LabelTable.TABLE_NAME + " (" +
        DatabaseContract.LabelTable.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        DatabaseContract.LabelTable.TEXT + " TEXT NOT NULL)");
    // Note-Label relationship
    db.execSQL("CREATE TABLE " + DatabaseContract.NoteLabelTable.TABLE_NAME + " (" +
        DatabaseContract.NoteLabelTable.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        DatabaseContract.NoteLabelTable.NOTE_ID + " INTEGER NOT NULL," +
        DatabaseContract.NoteLabelTable.LABEL_ID + " INTEGER NOT NULL)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.NoteTable.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.LabelTable.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.NoteLabelTable.TABLE_NAME);
    onCreate(db);
  }
}
