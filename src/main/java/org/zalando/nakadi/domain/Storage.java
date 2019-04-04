package org.zalando.nakadi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

public class Storage {
    public enum Type {
        KAFKA(KafkaConfiguration.class, Timeline.KafkaStoragePosition.class);
        private final Class configClass;
        private final Class<? extends Timeline.StoragePosition> positionClass;

        Type(final Class configClass, final Class<? extends Timeline.StoragePosition> positionClass) {
            this.configClass = configClass;
            this.positionClass = positionClass;
        }
    }

    public static class KafkaConfiguration {
        private String zkConnectionString;

        public KafkaConfiguration(
                @JsonProperty(value = "zk_connection_string") final String zkConnectionString) {
            this.zkConnectionString = zkConnectionString;
        }

        public String getZkConnectionString() {
            return zkConnectionString;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final KafkaConfiguration that = (KafkaConfiguration) o;
            return Objects.equals(zkConnectionString, that.zkConnectionString);
        }

        @Override
        public int hashCode() {
            return zkConnectionString.hashCode();
        }

        @Override
        public String toString() {
            return "KafkaConfiguration{zkConnectionString='" + zkConnectionString + "'}";
        }
    }

    private String id;
    @JsonProperty("storage_type")
    private Type type;
    private Object configuration;

    public Storage() {
    }

    public Storage(final String id, final Type type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public KafkaConfiguration getKafkaConfiguration() {
        return getConfiguration(KafkaConfiguration.class);
    }

    public <T> T getConfiguration(final Class<T> clazz) {
        if (!clazz.isAssignableFrom(configuration.getClass())) {
            throw new IllegalStateException("Can not cast configuration " + configuration + " to class " + clazz);
        }
        return (T) configuration;
    }

    public <T> void setConfiguration(final T configuration) {
        if (getType().configClass != configuration.getClass()) {
            throw new IllegalStateException("Only configuration of type " + getType().configClass + " accepted");
        }
        this.configuration = configuration;
    }

    public void parseConfiguration(final ObjectMapper mapper, final String data) throws IOException {
        this.configuration = mapper.readValue(data, getType().configClass);
    }

    @Nullable
    public Timeline.StoragePosition restorePosition(
            final ObjectMapper mapper, @Nullable final String data) throws IOException {
        return null == data ? null : mapper.readValue(data, getType().positionClass);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Storage)) {
            return false;
        }

        final Storage that = (Storage) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type) &&
                Objects.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", configuration=" + configuration +
                '}';
    }

}
