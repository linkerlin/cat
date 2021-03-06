package com.dianping.cat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Properties {
    public static StringPropertyAccessor forString() {
        return new StringPropertyAccessor();
    }

    public static class MapPropertyProvider<T> implements PropertyProvider {
        private String name;
        private Map<String, T> map;

        public MapPropertyProvider(Map<String, T> map) {
            this.map = map;
        }

        @Override
        public Object getProperty(String name) {
            T value = null;

            if (this.name != null) {
                name = this.name;
            }

            if (map != null) {
                value = map.get(name);
            }

            return value;
        }

        public MapPropertyProvider<T> setName(String name) {
            this.name = name;
            return this;
        }
    }

    public static abstract class PropertyAccessor<T> {
        private List<PropertyProvider> providers = new ArrayList<PropertyProvider>();

        public PropertyAccessor<T> fromEnv() {
            return fromEnv(null);
        }

        public PropertyAccessor<T> fromEnv(String name) {
            providers.add(new SystemPropertyProvider(false, true).setName(name));

            return this;
        }

        public PropertyAccessor<T> fromMap(Map<String, T> map) {
            return fromMap(map, null);
        }

        public PropertyAccessor<T> fromMap(Map<String, T> map, String name) {
            providers.add(new MapPropertyProvider<T>(map).setName(name));

            return this;
        }

        public PropertyAccessor<T> fromSystem() {
            return fromSystem(null);
        }

        public PropertyAccessor<T> fromSystem(String name) {
            providers.add(new SystemPropertyProvider(true, false).setName(name));

            return this;
        }

        protected Object getProperty(String name) {
            Object value = null;

            for (PropertyProvider provider : providers) {
                value = provider.getProperty(name);

                if (value != null) {
                    break;
                }
            }

            return value;
        }

        public abstract T getProperty(String name, T defaultValue);
    }

    public interface PropertyProvider {
        Object getProperty(String name);
    }

    public static class StringPropertyAccessor extends PropertyAccessor<String> {
        @Override
        public String getProperty(String name, String defaultValue) {
            Object value = name == null ? null : getProperty(name);

            if (value == null) {
                return defaultValue;
            } else {
                return value.toString();
            }
        }
    }

    public static class SystemPropertyProvider implements PropertyProvider {
        private boolean properties;
        private boolean env;
        private String name;

        public SystemPropertyProvider(boolean properties, boolean env) {
            this.properties = properties;
            this.env = env;
        }

        @Override
        public Object getProperty(String name) {
            String value = null;

            if (this.name != null) {
                name = this.name;
            }

            if (properties) {
                value = System.getProperty(name);
            }

            if (value == null && env) {
                value = System.getenv(name);
            }

            return value;
        }

        public SystemPropertyProvider setName(String name) {
            this.name = name;
            return this;
        }
    }
}
