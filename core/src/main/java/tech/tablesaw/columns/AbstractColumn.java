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

package tech.tablesaw.columns;

import org.apache.commons.lang3.StringUtils;

import tech.tablesaw.store.ColumnMetadata;

import java.util.UUID;

/**
 * Partial implementation of the {@link Column} interface
 */
public abstract class AbstractColumn implements Column {

    private String id;

    private String name;

    private String comment;

    public AbstractColumn(String name) {
        setName(name);
        this.comment = "";
        this.id = UUID.randomUUID().toString();
    }

    public AbstractColumn(ColumnMetadata metadata) {
        setName(metadata.getName());
        this.comment = "";
        this.id = metadata.getId();
    }

    public String name() {
        return name;
    }

    public String id() {
        return id;
    }

    @Override
    public String metadata() {
        return columnMetadata().toJson();
    }

    @Override
    public Column setName(String name) {
        this.name = name.trim();
        return this;
    }

    public abstract void appendCell(String stringvalue);

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public ColumnMetadata columnMetadata() {
        return new ColumnMetadata(this);
    }

    /**
     * Returns the width of the column in characters, for printing
     */
    @Override
    public int columnWidth() {

        int width = name().length();
        for (int rowNum = 0; rowNum < size(); rowNum++) {
            width = Math.max(width, StringUtils.length(getString(rowNum)));
        }
        return width;
    }

    @Override
    public Column difference() {
        throw new UnsupportedOperationException("difference() method not supported for all data types");
    }
}