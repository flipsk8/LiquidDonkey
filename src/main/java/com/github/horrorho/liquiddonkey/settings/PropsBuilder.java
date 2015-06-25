/*
 * The MIT License
 *
 * Copyright 2015 Ahseya.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.horrorho.liquiddonkey.settings;

import com.github.horrorho.liquiddonkey.iofunction.IOSupplier;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.READ;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropsBuilder.
 *
 * @author Ahseya
 * @param <E> enum type
 */
@NotThreadSafe
public class PropsBuilder<E extends Enum<E>> {

    private static final Logger logger = LoggerFactory.getLogger(PropsBuilder.class);

    public static <E extends Enum<E>> PropsBuilder<E> fromDefaults(Class<E> type, Function<E, String> defaultValue) {
        return new PropsBuilder(type, defaultValue, null).defaults();
    }

    private final Class<E> type;
    private final Function<E, String> defaultValue;
    private Props<E> props;

    PropsBuilder(Class<E> type, Function<E, String> defaultValue, Props<E> props) {
        this.type = Objects.requireNonNull(type);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.props = props;
    }

    public PropsBuilder<E> resource(E url) {
        if (!props.contains(url)) {
            logger.warn("-- resource() > missing url property: {}", url);
            return this;
        }
        logger.debug("-- resource() > url: {} / {}", url, props.get(url));
        return inputStream(() -> this.getClass().getResourceAsStream(props.get(url)));
    }

    public PropsBuilder<E> path(E path) {
        if (!props.contains(path)) {
            logger.warn("-- path() > missing path property: {}", path);
            return this;
        }
        logger.debug("-- path() > path: {} / {}", path, props.get(path));
        return inputStream(() -> Files.newInputStream(Paths.get(props.get(path)), READ));
    }

    public PropsBuilder<E> inputStream(IOSupplier<InputStream> supplier) {
        props = Props.newInstance(type, props);
        Properties properties = new Properties();

        try (InputStream inputStream = supplier.get()) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                logger.warn("-- inputStream() > null InputStream");
            }
        } catch (IOException ex) {
            logger.warn("-- inputStream() > exception: {}", ex);
        }
        props.addAll(properties);
        return this;
    }

    public Props<E> build() {
        return props;
    }

    PropsBuilder<E> defaults() {
        props = Props.newInstance(type, props);
        Stream.of(type.getEnumConstants())
                .filter(property -> defaultValue.apply(property) != null)
                .forEach(property -> props.put(property, defaultValue.apply(property)));
        return this;
    }
}