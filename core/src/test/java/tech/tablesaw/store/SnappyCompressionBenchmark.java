/*
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

package tech.tablesaw.store;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.store.StorageManager;
import tech.tablesaw.testutil.DirectoryUtils;
import tech.tablesaw.testutil.NanoBench;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class SnappyCompressionBenchmark {

    @Test
    public void testFloat() {

        File TEST_FOLDER = Paths.get("testfolder").toFile();
        Table t = Table.create("Test");
        final FloatColumn c = new FloatColumn("fc");
        t.addColumn(c);

        Path path = Paths.get("testfolder");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 1_000_000; i++) {
            c.append((float) Math.random());
        }

        NanoBench nanoBench = NanoBench.create();
        nanoBench.warmUps(5).measurements(20).cpuAndMemory().measure("Compression and file writing",
                () -> {
                    try {
                        StorageManager.writeColumn(TEST_FOLDER + File.separator + "foo", c);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Compressed size: " + DirectoryUtils.folderSize(TEST_FOLDER));
    }

    @Test
    public void testInt() {

        File TEST_FOLDER = Paths.get("testfolder").toFile();
        Table t = Table.create("Test");
        final IntColumn c = new IntColumn("fc", 10_000_000);
        t.addColumn(c);
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

        for (int i = 0; i < 10_000_000; i++) {
            c.append(randomDataGenerator.nextInt(0, 1_000_000));
        }

        Path path = Paths.get("testfolder");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NanoBench nanoBench = NanoBench.create();

        nanoBench.warmUps(5).measurements(20).cpuAndMemory().measure("Compression",
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StorageManager.writeColumn(TEST_FOLDER + File.separator + "foo", c);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        System.out.println("Compressed size: " + DirectoryUtils.folderSize(TEST_FOLDER));
    }
}
