/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.iceberg;

import com.netflix.iceberg.io.OutputFile;

/**
 * SPI interface to abstract table metadata access and updates.
 */
public interface TableOperations {

  /**
   * Return the currently loaded table metadata, without checking for updates.
   *
   * @return table metadata
   */
  TableMetadata current();

  /**
   * Return the current table metadata after checking for updates.
   *
   * @return table metadata
   */
  TableMetadata refresh();

  /**
   * Replace the base table metadata with a new version.
   * <p>
   * This method should implement and document atomicity guarantees.
   * <p>
   * Implementations must check that the base metadata is current to avoid overwriting updates.
   * Once the atomic commit operation succeeds, implementations must not perform any operations that
   * may fail because failure in this method cannot be distinguished from commit failure.
   *
   * @param base     table metadata on which changes were based
   * @param metadata new table metadata with updates
   */
  void commit(TableMetadata base, TableMetadata metadata);

  /**
   * @return a {@link com.netflix.iceberg.FileIO} to read and write table data and metadata files
   */
  FileIO io();

  /**
   * Given the name of a metadata file, obtain the full path of that file using an appropriate base
   * location of the implementation's choosing.
   * <p>
   * The file may not exist yet, in which case the path should be returned as if it were to be created
   * by e.g. {@link FileIO#newOutputFile(String)}.
   */
  String metadataFileLocation(String fileName);

  /**
   * Create a new ID for a Snapshot
   *
   * @return a long snapshot ID
   */
  long newSnapshotId();

}
