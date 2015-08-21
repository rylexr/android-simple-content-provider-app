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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
  public static final String AUTHORITY = "com.tinbytes.simplecontentproviderapp.SimpleContentProvider";
  public static final String DB_NAME = "simplecontentprovider.db";
  public static final int DB_VERSION = 1;

  public interface NoteColumns {
    String ID = BaseColumns._ID;
    String TEXT = "note";
    String CREATED_ON = "created_on";
  }

  public static final class NoteTable implements NoteColumns {
    public static final String TABLE_NAME = "note";
    public static final String URI_PATH = "note";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + URI_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + URI_PATH;
    public static final String CONTENT_NOTE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + URI_PATH;

    private NoteTable() {
    }
  }

  public interface LabelColumns {
    String ID = BaseColumns._ID;
    String TEXT = "label";
  }

  public static final class LabelTable implements LabelColumns {
    public static final String TABLE_NAME = "label";
    public static final String URI_PATH = "label";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + URI_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + URI_PATH;
    public static final String CONTENT_LABEL_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + URI_PATH;

    private LabelTable() {
    }
  }

  public interface NoteLabelColumns {
    String ID = BaseColumns._ID;
    String NOTE_ID = "note_id";
    String LABEL_ID = "label_id";
  }

  public static final class NoteLabelTable implements NoteLabelColumns {
    public static final String TABLE_NAME = "note_label";
    public static final String URI_PATH = "note_label";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NoteTable.URI_PATH + "/#/" + LabelTable.URI_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + URI_PATH;

    private NoteLabelTable() {
    }
  }
}
