package arlot.user.display;

import arlot.data.Data;
import arlot.data.collect.DataList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Holds the processing and handling of resources.
 * @see Resource
 */
public final class Resources {

    /**
     * Grants easy access to the needed data attributed to the retrieval of resources.
     *
     * <P>No {@code ResourceURL} should have a {@link ResourceType ResourceType} of
     * {@link ResourceType#UNKNOWN UNKNOWN}, instead any resources that have an unknown
     * origin should have a {@link ResourceType ResourceType} of
     * {@link ResourceType#REMOTE REMOTE}.</P>
     *
     * @param url The {@link URL} that represents this {@code ResourceURL}.
     * @param resourceType The relative location the {@link Resource} was found to be in.
     */
    public record ResourceURL(URL url, ResourceType resourceType) {
        /**
         * Returns a {@link URLConnection} instance that
         * represents a connection to the remote object referred to by the
         * {@link #url}.
         *
         * <P>It should be noted that a URLConnection instance does not establish
         * the actual network connection on creation. This will happen only when
         * calling {@linkplain URLConnection#connect()}.</P>
         *
         * @return a {@link URLConnection URLConnection} linking to the URL.
         * @throws IOException if an I/O exception occurs.
         * @see #url()#openConnection()
         */
        public URLConnection openConnection() throws IOException {
            return url.openConnection();
        }

        /**
         * Opens a connection to this {@code ResourceURL} and returns an
         * {@code InputStream} for reading from that connection. This
         * method is a shorthand for:
         * <blockquote><pre>
         *     openConnection().getInputStream()
         * </pre></blockquote>
         *
         * @return an input stream for reading from the URL connection.
         * @throws IOException if an I/O exception occurs.
         * @see #openConnection()
         * @see URLConnection#getInputStream()
         */
        public InputStream openStream() throws IOException {
            return openConnection().getInputStream();
        }

        /**
         * Gets the path part of the {@link #url}.
         * @return the path part of the {@link #url},
         * or an empty string if one does not exist.
         */
        public String getPath() {
            return url.getPath();
        }

        /**
         * Gets the query part of the {@link #url}.
         * @return the query part of the {@link #url},
         * or {@code null} if one does not exist.
         */
        public String getQuery() {
            return url.getQuery();
        }

        /**
         * Gets the file name of the {@link #url}.
         * The returned file portion will be the same as {@link #getPath()},
         * plus the concatenation of the value of {@link #getQuery()}, if any.
         * If there is no query portion, this method and {@link #getPath()}
         * will return identical results.
         *
         * @return the path name of the {@link #url},
         * or an empty string if one does not exist.
         */
        public String getFile() {
            return url.getFile();
        }
    }

    /**
     * The {@code Resource} class is the container where each individual resources are stored.
     * @see ResourceURL
     */
    public static class Resource {

        /**
         * The name to this Resource.
         */
        private final String name;

        /**
         * Holds the {@link ResourceURL} to this Resource as {@link Data}.
         */
        private final DataList<ResourceURL> sources;

        /**
         * The Resource constructor creates a storage container for easy to fetch resources.
         * @param name The name to this Resource.
         * @param sources The {@link ResourceURL} of this Resource.
         */
        public Resource(String name, ResourceURL... sources) {
            this.name = name;
            this.sources = new DataList<>(sources.length);
            for (ResourceURL source : sources) {
                this.sources.add(new Data<>(source, false));
            }
        }

        /**
         * Gets the name to this Resource.
         */
        public final String name() {
            return name;
        }

        /**
         * Gets all the sources provided to this Resource.
         * @return A {@link ResourceURL} {@link DataList}.
         */
        public DataList<ResourceURL> sources() {
            return sources;
        }
    } // end of Resource

    // properties
    /**
     * Holds the name that is attributed to this resource holder.
     */
    private final String name;
    private final HashMap<String, Resource> resourceHashMap;

    // constructor

    /**
     * Constructs a new empty resource holder.
     * @param name What to call this resource holder.
     */
    public Resources(String name) {
        this.name = name;
        resourceHashMap = new HashMap<>();
    }

    // methods

    /**
     *
     * @return The name of this resource holder.
     */
    public String name() {
        return name;
    }

    public Resource get(String name) {
        return resourceHashMap.get(name);
    }

    public Resource[] resources() {
        return resourceHashMap.values().toArray(new Resource[0]);
    }

    public String[] resourceNames() {
        return resourceHashMap.keySet().toArray(new String[0]);
    }

    public void add(Resource resource) {
        resourceHashMap.put(resource.name(), resource);
    }

    public void add(String name, ResourceURL... sources) {
        add(new Resource(name, sources));
    }

    public void addAll(Resource... resources) {
        for (Resource resource : resources) {
            resourceHashMap.put(resource.name(), resource);
        }
    }

    /**
     * Checks to see if this resource holder has any stored resources
     * @return true if this resource holder has resources stored, otherwise false.
     */
    public boolean isEmpty() {
        return resourceHashMap.isEmpty();
    }

    // static resourcing
    public static URL getResource(String name, ClassLoader loader) {
        return loader.getResource(name);
    }
    private static String pathPass(String path1, String path2) {
        String path;
        if (pathCompare(path1, path2))
            return path1;
        if (path1.startsWith("/") || path1.startsWith("\\")) {
            if (path2.endsWith("/") || path2.endsWith("\\")) {
                path = path2.substring(0, path2.length()-1)+path1;
            } else {
                path = path2+path1;
            }
        } else {
            if (path2.endsWith("/") || path2.endsWith("\\")) {
                path = path2+path1;
            } else {
                path = path2+"/"+path1;
            }
        }
        return path;
    }
    private static boolean pathCompare(String path1, String path2) {
        return filePath(path1).startsWith(filePath(path2));
    }
    private static String filePath(String path) {
        return path.replace("/", FileSystems.getDefault().getSeparator())
                .replace("\\", FileSystems.getDefault().getSeparator());
    }

    private static URL getRemoteResource(String file, String[] startPaths) {
        String[] cases = new String[] {
                "",
                "target/",
                "target/classes/",
                "target/test-classes/",
                "src/main/resources/",
                "src/test/resources/",
                "src/",
                "src/main/",
                "src/main/java/",
                "src/test/java/",

                "target/classes/arlot/",
                "target/test-classes/arlot/",
                "src/main/resources/arlot/",
                "src/test/resources/arlot/",
                "src/main/java/arlot/",
                "src/test/java/arlot/",


        };
        if (startPaths == null || Arrays.stream(startPaths).toList().isEmpty()) {
            startPaths = new String[] {""};
        }
        URL source = null;
        for (String sPath : startPaths) {
            for (String cPath : cases) {
                String path = filePath(
                        pathPass(
                                pathPass(
                                        pathPass(file, sPath),
                                        cPath
                                ),
                                System.getProperty("user.dir")
                        )
                );
                try {
                    File f = new File(path);

                    if (f.exists()) {
                        source = f.toURI().toURL();
                        //System.out.println(path);//f.getPath());
                    }
                } catch (MalformedURLException ignore) {}
                if (source != null) {
                    break;
                }
            }
        }
        return source;
    }

    /**
     * Finds the resource with the given file path via the usage of the stated
     * {@link ResourceType}.
     *
     * @param file The file path to the resource.
     * @param type The {@link ResourceType} that is used for retrieving a resource.
     * @return a {@link Resource} if the provided file is found, otherwise null.
     */
    public static Resource getResource(String file, ResourceType type) {
        Objects.requireNonNull(file);
        if (type == null) {
            type = ResourceType.UNKNOWN;
        }
        ClassLoader[] loaders;
        Resource resource = null;
        URL source = null;
        String[] startPaths = null;
        if (type == ResourceType.REMOTE) {
            startPaths = new String[] {
                    ""
            };
        } else if (type == ResourceType.UNKNOWN) {
            for (ResourceType resourceType : ResourceType.values()) {
                if (resourceType != type) {
                    resource = getResource(file, resourceType);
                    if (resource != null) {
                        break;
                    }
                }
            }
        } else {
            if (type == ResourceType.INTERNAL) {
                startPaths = new String[] {
                        "target/classes/arlot/",
                        "target/classes/arlot/user/",
                        "target/classes/arlot/user/display/",
                        "target/classes/arlot/user/display/images/",
                        "target/classes/arlot/user/display/controllers/",
                        "target/classes/arlot/data/",
                        "target/classes/arlot/roleplay/",
                        "target/classes/arlot/roleplay/info/",
                        "target/classes/arlot/error/",
                        "target/classes/arlot/math/",
                        "target/classes/arlot/protection/",
                        "target/classes/arlot/time/",

                        "arlot/target/classes/arlot/",
                        "arlot/target/classes/arlot/user/",
                        "arlot/target/classes/arlot/user/display/",
                        "arlot/target/classes/arlot/user/display/images/",
                        "arlot/target/classes/arlot/user/display/controllers/",
                        "arlot/target/classes/arlot/data/",
                        "arlot/target/classes/arlot/roleplay/",
                        "arlot/target/classes/arlot/roleplay/info/",
                        "arlot/target/classes/arlot/error/",
                        "arlot/target/classes/arlot/math/",
                        "arlot/target/classes/arlot/protection/",
                        "arlot/target/classes/arlot/time/",

                        "src/main/resources/classes/arlot/user/display/",
                        "src/main/resources/classes/arlot/user/display/images/",
                        "src/main/java/arlot/user/",
                        "src/main/java/arlot/user/display/",
                        "src/main/java/arlot/user/display/controllers/",
                        "src/main/java/arlot/data/",
                        "src/main/java/arlot/roleplay/",
                        "src/main/java/arlot/roleplay/info/",
                        "src/main/java/arlot/error/",
                        "src/main/java/arlot/math/",
                        "src/main/java/arlot/protection/",
                        "src/main/java/arlot/time/",

                        "arlot/src/main/resources/classes/arlot/user/display/",
                        "arlot/src/main/resources/classes/arlot/user/display/images/",
                        "arlot/src/main/java/arlot/user/",
                        "arlot/src/main/java/arlot/user/display/",
                        "arlot/src/main/java/arlot/user/display/controllers/",
                        "arlot/src/main/java/arlot/data/",
                        "arlot/src/main/java/arlot/roleplay/",
                        "arlot/src/main/java/arlot/roleplay/info/",
                        "arlot/src/main/java/arlot/error/",
                        "arlot/src/main/java/arlot/math/",
                        "arlot/src/main/java/arlot/protection/",
                        "arlot/src/main/java/arlot/time/",
                };
                loaders = new ClassLoader[] {
                        Resources.class.getClassLoader()
                };
            } else if (type == ResourceType.EXTERNAL) {
                startPaths = new String[] {
                        "target/classes/",
                        "target/",
                        "src/main/resources/",
                        "src/resources/",
                        "resources/",
                        "main/resources/",
                        "src/main/java/",
                        "src/main/",
                        "src/",
                        "main/",
                };
                loaders = new ClassLoader[] {
                        Thread.currentThread().getContextClassLoader()
                };
            } else if (type == ResourceType.RUNTIME) {
                loaders = new ClassLoader[] {
                        ClassLoader.getSystemClassLoader(),
                        ClassLoader.getPlatformClassLoader()
                };
            } else {
                throw new IllegalArgumentException("The provided ResourceType isn't allowed.");
            }
            for (ClassLoader loader : loaders) {
                if (loader == null) {
                    continue;
                }
                try {
                    source = getResource(file, loader);
                } catch (Exception e) {
                    break;
                }
                if (source != null) {
                    break;
                }
            }
        }
        if (resource == null && source == null &&
                type != ResourceType.RUNTIME && type != ResourceType.UNKNOWN) {
            source = getRemoteResource(file, startPaths);
        }
        if (resource == null && source != null) {
            resource = new Resource(file, new ResourceURL(source, type));
        }
        return resource;
    }

    /**
     * Gets a resource as a {@link ResourceURL} at the provided file path.
     * <br>
     * If the stated resource cannot be retrieved by the system class loader,
     * then the resource will be pulled via calling the file path as a {@link File}
     * and checking if the file exists, if file exists then the {@link ResourceURL} to the resource
     * will be returned.
     * <br>
     * If the file cannot be found (or doesn't exist) then null will be returned.
     * @param file The file path to the resource.
     * @return The {@link ResourceURL} to the resource if found, otherwise null.
     */
    public static ResourceURL loadResource(String file) {
        return getResource(file, ResourceType.UNKNOWN).sources().getFirst();
    }
    /**
     * Gets a resource as a {@link InputStream} at the provided file path.
     * <br>
     * The search order is described in the documentation for {@link #loadResource(String)}.
     * <br>
     * If the file cannot be found (or doesn't exist) then null will be returned.
     * @param file The file path to the resource.
     * @return The {@link InputStream} to the resource if found, otherwise null.
     */
    public static InputStream loadResourceAsStream(String file) {
        ResourceURL url = loadResource(file);
        if (url != null) {
            try {
                return url.openStream();
            } catch (IOException ignore) {}
        }
        return null;
    }

    // enums

    /**
     * Resources that are stored by the {@link arlot} package.
     */
    public enum INTERNAL {
        ;

        /**
         * The internal FXML files provided by the {@link arlot} package.
         */
        public enum FXML {
            DEFAULT("default.fxml", "controllers/Default.class");

            private final Resource fxmlResource;
            private final Resource controllerResource;

            FXML(String fxml, String controller) {
                /*String path = "arlot/user/display";
                if (fxml.startsWith("/")) {
                    fxml = "/"+path+fxml;
                } else {
                    fxml = path+"/"+fxml;
                }
                if (controller.startsWith("/")) {
                    controller = "/"+path+controller;
                } else {
                    controller = path+"/"+controller;
                }*/
                Instant start = Instant.now();
                fxmlResource = getResource(fxml, ResourceType.INTERNAL);
                controllerResource = getResource(controller, ResourceType.INTERNAL);
                Instant end = Instant.now();
                //System.out.println(Duration.between(start, end));
            }

            /**
             * The FXML file to this FXML resource.
             * @return The {@link ResourceURL} of this FXML resource.
             */
            public ResourceURL fxml() {
                return fxmlResource.sources().getFirst();
            }

            /**
             * The controller file to this FXML resource.
             * @return The {@link ResourceURL} of this FXML controller resource.
             */
            public ResourceURL controller() {
                return controllerResource.sources().getFirst();
            }
        }
    } // end of INTERNAL Resources enum

    public enum ResourceType {
        /**
         * Defines that a resource is predefined by the {@link arlot} package.
         */
        INTERNAL,
        /**
         * Defines that a resource is outside the predefined resources.
         */
        EXTERNAL,
        /**
         * Defines that a resource is accessible to {@link ClassLoader built-in ClassLoaders}.
         */
        RUNTIME,
        /**
         * Defines that a resource is located outside the scopes of
         * all other {@code ResourceTypes}.
         * This deems that the resource can only be retrieved via file handling APIs.
         */
        REMOTE,
        /**
         * Used <b>ONLY</b> in the {@link Resources#getResource(String, ResourceType)}
         * method for getting resources that you are not sure of it's {@code ResourceType}.
         */
        UNKNOWN
    } // end of ResourceType
} // end of Resources
