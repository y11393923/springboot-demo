package com.example.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesSupport {

    private Properties props;

    public PropertiesSupport(Properties props) {
        this.props = props;
    }

    public PropertiesSupport(InputStream inputStream) throws IOException {
        props = new Properties();
        props.load(new InputStreamReader(inputStream));
    }

    /**
     * Gets the named property as a boolean value. If the property matches the string {@code "true"} (case-insensitive),
     * then it is returned as the boolean value {@code true}. Any other non-{@code null} text in the property is
     * considered {@code false}.
     *
     * @param name the name of the property to look up
     * @return the boolean value of the property or {@code false} if undefined.
     */
    public boolean getBoolean(final String name) {
        return this.getBoolean(name, false);
    }

    /**
     * Gets the named property as a boolean value.
     *
     * @param name the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the boolean value of the property or {@code defaultValue} if undefined.
     */
    public boolean getBoolean(final String name, final boolean defaultValue) {
        final String prop = getString(name);
        return (prop == null) ? defaultValue : ("true".equalsIgnoreCase(prop) || "yes".equalsIgnoreCase(prop));
    }

    /**
     * Gets the named property as a double.
     *
     * @param name the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed double value of the property or {@code defaultValue} if it was undefined or could not be parsed.
     */
    public double getDouble(final String name, final double defaultValue) {
        final String prop = getString(name);
        if (prop != null) {
            try {
                return Double.parseDouble(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Gets the named property as an integer.
     *
     * @param name the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed integer value of the property or {@code defaultValue} if it was undefined or could not be
     *         parsed.
     */
    public int getInteger(final String name, final int defaultValue) {
        final String prop = getString(name);
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Gets the named property as a long.
     *
     * @param name the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed long value of the property or {@code defaultValue} if it was undefined or could not be parsed.
     */
    public long getLong(final String name, final long defaultValue) {
        final String prop = getString(name);
        if (prop != null) {
            try {
                return Long.parseLong(prop);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }


    public String[] getArrayString(final String name, String delimiter) {
        final String prop = getString(name);
        if (prop != null) {
            return StringUtils.split(prop, delimiter);
        }
        return null;
    }

    /**
     * Gets the named property as a String.
     *
     * @param name the name of the property to look up
     * @return the String value of the property or {@code null} if undefined.
     */
    public String getString(final String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (final SecurityException ignored) {
            // Ignore
        }
        return prop == null ? props.getProperty(name) : prop;
    }


    /**
     * Gets the named property as a String.
     *
     * @param name the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the String value of the property or {@code defaultValue} if undefined.
     */
    public String getString(final String name, final String defaultValue) {
        final String prop = getString(name);
        return (prop == null) ? defaultValue : prop;
    }
}
