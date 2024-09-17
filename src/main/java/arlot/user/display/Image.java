package arlot.user.display;

import com.sun.javafx.tk.PlatformImage;
import com.sun.javafx.util.DataURI;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class Image {
    private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
    private final javafx.scene.image.Image master;

    private String url;
    private InputStream inputSource;

    private double requestedWidth;
    private double requestedHeight;

    private DoublePropertyImpl width;
    private DoublePropertyImpl height;

    private ObjectPropertyImpl<PlatformImage> platformImage;

    private boolean preserveRatio;
    private boolean smooth;
    private boolean backgroundLoading;
    public Image(String url) {
        this.master = new javafx.scene.image.Image(url);
        init(url, null, 0.0, 0.0, false, false, false);
    }
    public Image(String url, boolean backgroundLoading) {
        this.master = new javafx.scene.image.Image(url, backgroundLoading);
        init(url, null, 0.0, 0.0, false, false, backgroundLoading);
    }
    public Image(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        this.master = new javafx.scene.image.Image(url, requestedWidth, requestedHeight, preserveRatio, smooth);
        init(url, null, requestedWidth, requestedHeight, preserveRatio, smooth, false);
    }
    public Image(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth, boolean backgroundLoading) {
        this.master = new javafx.scene.image.Image(url, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading);
        init(url, null, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading);
    }
    public Image(InputStream inputStream) {
        this.master = new javafx.scene.image.Image(inputStream);
        init(null, inputStream, 0.0, 0.0, false, false, false);
    }
    public Image(InputStream inputStream, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        this.master = new javafx.scene.image.Image(inputStream, requestedWidth, requestedHeight, preserveRatio, smooth);
        init(null, inputStream, requestedWidth, requestedHeight, preserveRatio, smooth, false);
    }

    protected void init(String url, InputStream inputStream, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth, boolean backgroundLoading) {
        this.url = url!=null ? validateUrl(url) : null;
        this.inputSource = inputStream;
        this.requestedWidth = requestedWidth;
        this.requestedHeight = requestedHeight;
        this.preserveRatio = preserveRatio;
        this.smooth = smooth;
        this.backgroundLoading = backgroundLoading;
    }

    public javafx.scene.image.Image getMaster() {
        return master;
    }

    public final String getUrl() {
        return this.url;
    }

    final InputStream getInputSource() {
        return this.inputSource;
    }

    public ImageView getView() {
        return new ImageView(this.getMaster());
    }

    public double getRequestedHeight() {
        return requestedHeight;
    }
    public void setRequestedHeight(double requestedHeight) {
        this.requestedHeight = requestedHeight;
    }

    public double getRequestedWidth() {
        return requestedWidth;
    }
    public void setRequestedWidth(double requestedWidth) {
        this.requestedWidth = requestedWidth;
    }

    public final double getWidth() {
        return this.width == null ? 0.0 : this.width.get();
    }
    public final ReadOnlyDoubleProperty widthProperty() {
        return this.widthPropertyImpl();
    }
    private DoublePropertyImpl widthPropertyImpl() {
        if (this.width == null) {
            this.width = new DoublePropertyImpl("width");
        }

        return this.width;
    }

    public final double getHeight() {
        return this.height == null ? 0.0 : this.height.get();
    }
    public final ReadOnlyDoubleProperty heightProperty() {
        return this.heightPropertyImpl();
    }
    private DoublePropertyImpl heightPropertyImpl() {
        if (this.height == null) {
            this.height = new DoublePropertyImpl("height");
        }

        return this.height;
    }

    public final boolean isPreserveRatio() {
        return this.preserveRatio;
    }
    public final boolean isSmooth() {
        return this.smooth;
    }
    public final boolean isBackgroundLoading() {
        return this.backgroundLoading;
    }

    final Object getPlatformImage() {
        return this.platformImage == null ? null : this.platformImage.get();
    }

    final ReadOnlyObjectProperty<PlatformImage> acc_platformImageProperty() {
        return this.platformImagePropertyImpl();
    }

    private ObjectPropertyImpl<PlatformImage> platformImagePropertyImpl() {
        if (this.platformImage == null) {
            this.platformImage = new ObjectPropertyImpl<PlatformImage>("platformImage");
        }

        return this.platformImage;
    }

    void pixelsDirty() {
        this.platformImagePropertyImpl().fireValueChangedEvent();
    }

    private static String validateUrl(String var0) {
        if (var0 == null) {
            throw new NullPointerException("URL must not be null");
        } else if (var0.trim().isEmpty()) {
            throw new IllegalArgumentException("URL must not be empty");
        } else {
            try {
                if (!URL_QUICKMATCH.matcher(var0).matches()) {
                    ClassLoader var1 = Thread.currentThread().getContextClassLoader();
                    URL var2;
                    if (var0.charAt(0) == '/') {
                        var2 = var1.getResource(var0.substring(1));
                    } else {
                        var2 = var1.getResource(var0);
                    }

                    if (var2 == null) {
                        throw new IllegalArgumentException("Invalid URL or resource not found");
                    } else {
                        return var2.toString();
                    }
                } else {
                    return DataURI.matchScheme(var0) ? var0 : (new URL(var0)).toString();
                }
            } catch (MalformedURLException | IllegalArgumentException var3) {
                throw new IllegalArgumentException(constructDetailedExceptionMessage("Invalid URL", var3), var3);
            }
        }
    }

    private static String constructDetailedExceptionMessage(String var0, Throwable var1) {
        if (var1 == null) {
            return var0;
        } else {
            String var2 = var1.getMessage();
            return constructDetailedExceptionMessage(var2 != null ? var0 + ": " + var2 : var0, var1.getCause());
        }
    }

    private final class DoublePropertyImpl extends ReadOnlyDoublePropertyBase {
        private final String name;
        private double value;

        public DoublePropertyImpl(String var2) {
            this.name = var2;
        }

        public void store(double var1) {
            this.value = var1;
        }

        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        public double get() {
            return this.value;
        }

        public Object getBean() {
            return Image.this;
        }

        public String getName() {
            return this.name;
        }
    }

    private final class ObjectPropertyImpl<T> extends ReadOnlyObjectPropertyBase<T> {
        private final String name;
        private T value;
        private boolean valid = true;

        public ObjectPropertyImpl(String var2) {
            this.name = var2;
        }

        public void store(T var1) {
            this.value = var1;
        }

        public void set(T var1) {
            if (this.value != var1) {
                this.value = var1;
                this.markInvalid();
            }

        }

        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        private void markInvalid() {
            if (this.valid) {
                this.valid = false;
                this.fireValueChangedEvent();
            }

        }

        public T get() {
            this.valid = true;
            return this.value;
        }

        public Object getBean() {
            return Image.this;
        }

        public String getName() {
            return this.name;
        }
    }
}
