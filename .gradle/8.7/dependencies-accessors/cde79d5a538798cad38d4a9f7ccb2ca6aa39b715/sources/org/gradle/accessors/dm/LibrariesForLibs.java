package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final EspressoLibraryAccessors laccForEspressoLibraryAccessors = new EspressoLibraryAccessors(owner);
    private final ExtLibraryAccessors laccForExtLibraryAccessors = new ExtLibraryAccessors(owner);
    private final FirebaseLibraryAccessors laccForFirebaseLibraryAccessors = new FirebaseLibraryAccessors(owner);
    private final GoogleLibraryAccessors laccForGoogleLibraryAccessors = new GoogleLibraryAccessors(owner);
    private final NavigationLibraryAccessors laccForNavigationLibraryAccessors = new NavigationLibraryAccessors(owner);
    private final OkhttpLibraryAccessors laccForOkhttpLibraryAccessors = new OkhttpLibraryAccessors(owner);
    private final PlayLibraryAccessors laccForPlayLibraryAccessors = new PlayLibraryAccessors(owner);
    private final RetrofitLibraryAccessors laccForRetrofitLibraryAccessors = new RetrofitLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>activity</b> with <b>androidx.activity:activity</b> coordinates and
     * with version reference <b>activity</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getActivity() {
        return create("activity");
    }

    /**
     * Dependency provider for <b>appcompat</b> with <b>androidx.appcompat:appcompat</b> coordinates and
     * with version reference <b>appcompat</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAppcompat() {
        return create("appcompat");
    }

    /**
     * Dependency provider for <b>circleImageView</b> with <b>de.hdodenhof:circleimageview</b> coordinates and
     * with version reference <b>circleImageView</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getCircleImageView() {
        return create("circleImageView");
    }

    /**
     * Dependency provider for <b>constraintlayout</b> with <b>androidx.constraintlayout:constraintlayout</b> coordinates and
     * with version reference <b>constraintlayout</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getConstraintlayout() {
        return create("constraintlayout");
    }

    /**
     * Dependency provider for <b>fragment</b> with <b>androidx.fragment:fragment</b> coordinates and
     * with version reference <b>fragment</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getFragment() {
        return create("fragment");
    }

    /**
     * Dependency provider for <b>gson</b> with <b>com.google.code.gson:gson</b> coordinates and
     * with version reference <b>gson</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGson() {
        return create("gson");
    }

    /**
     * Dependency provider for <b>json</b> with <b>org.json:json</b> coordinates and
     * with version reference <b>json</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJson() {
        return create("json");
    }

    /**
     * Dependency provider for <b>junit</b> with <b>junit:junit</b> coordinates and
     * with version reference <b>junit</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJunit() {
        return create("junit");
    }

    /**
     * Dependency provider for <b>material</b> with <b>com.google.android.material:material</b> coordinates and
     * with version reference <b>material</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMaterial() {
        return create("material");
    }

    /**
     * Dependency provider for <b>picasso</b> with <b>com.squareup.picasso:picasso</b> coordinates and
     * with version reference <b>picasso</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getPicasso() {
        return create("picasso");
    }

    /**
     * Dependency provider for <b>preference</b> with <b>androidx.preference:preference</b> coordinates and
     * with version reference <b>preference</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getPreference() {
        return create("preference");
    }

    /**
     * Dependency provider for <b>volley</b> with <b>com.android.volley:volley</b> coordinates and
     * with version reference <b>volley</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getVolley() {
        return create("volley");
    }

    /**
     * Group of libraries at <b>espresso</b>
     */
    public EspressoLibraryAccessors getEspresso() {
        return laccForEspressoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>ext</b>
     */
    public ExtLibraryAccessors getExt() {
        return laccForExtLibraryAccessors;
    }

    /**
     * Group of libraries at <b>firebase</b>
     */
    public FirebaseLibraryAccessors getFirebase() {
        return laccForFirebaseLibraryAccessors;
    }

    /**
     * Group of libraries at <b>google</b>
     */
    public GoogleLibraryAccessors getGoogle() {
        return laccForGoogleLibraryAccessors;
    }

    /**
     * Group of libraries at <b>navigation</b>
     */
    public NavigationLibraryAccessors getNavigation() {
        return laccForNavigationLibraryAccessors;
    }

    /**
     * Group of libraries at <b>okhttp</b>
     */
    public OkhttpLibraryAccessors getOkhttp() {
        return laccForOkhttpLibraryAccessors;
    }

    /**
     * Group of libraries at <b>play</b>
     */
    public PlayLibraryAccessors getPlay() {
        return laccForPlayLibraryAccessors;
    }

    /**
     * Group of libraries at <b>retrofit</b>
     */
    public RetrofitLibraryAccessors getRetrofit() {
        return laccForRetrofitLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class EspressoLibraryAccessors extends SubDependencyFactory {

        public EspressoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>androidx.test.espresso:espresso-core</b> coordinates and
         * with version reference <b>espressoCore</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("espresso.core");
        }

    }

    public static class ExtLibraryAccessors extends SubDependencyFactory {

        public ExtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>junit</b> with <b>androidx.test.ext:junit</b> coordinates and
         * with version reference <b>junitVersion</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() {
            return create("ext.junit");
        }

    }

    public static class FirebaseLibraryAccessors extends SubDependencyFactory {
        private final FirebaseCrashlyticsLibraryAccessors laccForFirebaseCrashlyticsLibraryAccessors = new FirebaseCrashlyticsLibraryAccessors(owner);
        private final FirebaseUiLibraryAccessors laccForFirebaseUiLibraryAccessors = new FirebaseUiLibraryAccessors(owner);

        public FirebaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>auth</b> with <b>com.google.firebase:firebase-auth</b> coordinates and
         * with version reference <b>firebaseAuth</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("firebase.auth");
        }

        /**
         * Dependency provider for <b>bom</b> with <b>com.google.firebase:firebase-bom</b> coordinates and
         * with version reference <b>firebaseBom</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBom() {
            return create("firebase.bom");
        }

        /**
         * Dependency provider for <b>database</b> with <b>com.google.firebase:firebase-database</b> coordinates and
         * with version reference <b>firebaseDatabase</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDatabase() {
            return create("firebase.database");
        }

        /**
         * Dependency provider for <b>firestore</b> with <b>com.google.firebase:firebase-firestore</b> coordinates and
         * with version reference <b>firebaseFirestore</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFirestore() {
            return create("firebase.firestore");
        }

        /**
         * Dependency provider for <b>messaging</b> with <b>com.google.firebase:firebase-messaging</b> coordinates and
         * with version reference <b>firebaseMessaging</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMessaging() {
            return create("firebase.messaging");
        }

        /**
         * Dependency provider for <b>storage</b> with <b>com.google.firebase:firebase-storage</b> coordinates and
         * with version reference <b>firebaseStorage</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStorage() {
            return create("firebase.storage");
        }

        /**
         * Group of libraries at <b>firebase.crashlytics</b>
         */
        public FirebaseCrashlyticsLibraryAccessors getCrashlytics() {
            return laccForFirebaseCrashlyticsLibraryAccessors;
        }

        /**
         * Group of libraries at <b>firebase.ui</b>
         */
        public FirebaseUiLibraryAccessors getUi() {
            return laccForFirebaseUiLibraryAccessors;
        }

    }

    public static class FirebaseCrashlyticsLibraryAccessors extends SubDependencyFactory {

        public FirebaseCrashlyticsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>buildtools</b> with <b>com.google.firebase:firebase-crashlytics-buildtools</b> coordinates and
         * with version reference <b>firebaseCrashlyticsBuildtools</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBuildtools() {
            return create("firebase.crashlytics.buildtools");
        }

    }

    public static class FirebaseUiLibraryAccessors extends SubDependencyFactory {

        public FirebaseUiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>database</b> with <b>com.firebaseui:firebase-ui-database</b> coordinates and
         * with version reference <b>firebaseUiDatabase</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDatabase() {
            return create("firebase.ui.database");
        }

    }

    public static class GoogleLibraryAccessors extends SubDependencyFactory {

        public GoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>services</b> with <b>com.google.gms:google-services</b> coordinates and
         * with version reference <b>googleServices</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getServices() {
            return create("google.services");
        }

    }

    public static class NavigationLibraryAccessors extends SubDependencyFactory {

        public NavigationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>fragment</b> with <b>androidx.navigation:navigation-fragment</b> coordinates and
         * with version reference <b>navigationFragment</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFragment() {
            return create("navigation.fragment");
        }

        /**
         * Dependency provider for <b>ui</b> with <b>androidx.navigation:navigation-ui</b> coordinates and
         * with version reference <b>navigationUi</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getUi() {
            return create("navigation.ui");
        }

    }

    public static class OkhttpLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final OkhttpLoggingLibraryAccessors laccForOkhttpLoggingLibraryAccessors = new OkhttpLoggingLibraryAccessors(owner);

        public OkhttpLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>okhttp</b> with <b>com.squareup.okhttp3:okhttp</b> coordinates and
         * with version <b>4.12.0</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("okhttp");
        }

        /**
         * Group of libraries at <b>okhttp.logging</b>
         */
        public OkhttpLoggingLibraryAccessors getLogging() {
            return laccForOkhttpLoggingLibraryAccessors;
        }

    }

    public static class OkhttpLoggingLibraryAccessors extends SubDependencyFactory {

        public OkhttpLoggingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>interceptor</b> with <b>com.squareup.okhttp3:logging-interceptor</b> coordinates and
         * with version reference <b>okhttpLoggingInterceptor</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getInterceptor() {
            return create("okhttp.logging.interceptor");
        }

    }

    public static class PlayLibraryAccessors extends SubDependencyFactory {
        private final PlayServicesLibraryAccessors laccForPlayServicesLibraryAccessors = new PlayServicesLibraryAccessors(owner);

        public PlayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>play.services</b>
         */
        public PlayServicesLibraryAccessors getServices() {
            return laccForPlayServicesLibraryAccessors;
        }

    }

    public static class PlayServicesLibraryAccessors extends SubDependencyFactory {

        public PlayServicesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>auth</b> with <b>com.google.android.gms:play-services-auth</b> coordinates and
         * with version reference <b>playServicesAuth</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuth() {
            return create("play.services.auth");
        }

    }

    public static class RetrofitLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final RetrofitConverterLibraryAccessors laccForRetrofitConverterLibraryAccessors = new RetrofitConverterLibraryAccessors(owner);

        public RetrofitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>retrofit</b> with <b>com.squareup.retrofit2:retrofit</b> coordinates and
         * with version reference <b>retrofit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("retrofit");
        }

        /**
         * Group of libraries at <b>retrofit.converter</b>
         */
        public RetrofitConverterLibraryAccessors getConverter() {
            return laccForRetrofitConverterLibraryAccessors;
        }

    }

    public static class RetrofitConverterLibraryAccessors extends SubDependencyFactory {

        public RetrofitConverterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>gson</b> with <b>com.squareup.retrofit2:converter-gson</b> coordinates and
         * with version reference <b>retrofitConverterGson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("retrofit.converter.gson");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>activity</b> with value <b>1.9.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getActivity() { return getVersion("activity"); }

        /**
         * Version alias <b>agp</b> with value <b>8.5.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAgp() { return getVersion("agp"); }

        /**
         * Version alias <b>appcompat</b> with value <b>1.7.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAppcompat() { return getVersion("appcompat"); }

        /**
         * Version alias <b>circleImageView</b> with value <b>3.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCircleImageView() { return getVersion("circleImageView"); }

        /**
         * Version alias <b>constraintlayout</b> with value <b>2.1.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getConstraintlayout() { return getVersion("constraintlayout"); }

        /**
         * Version alias <b>espressoCore</b> with value <b>3.6.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getEspressoCore() { return getVersion("espressoCore"); }

        /**
         * Version alias <b>firebaseAuth</b> with value <b>23.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseAuth() { return getVersion("firebaseAuth"); }

        /**
         * Version alias <b>firebaseBom</b> with value <b>33.1.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseBom() { return getVersion("firebaseBom"); }

        /**
         * Version alias <b>firebaseCrashlyticsBuildtools</b> with value <b>3.0.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseCrashlyticsBuildtools() { return getVersion("firebaseCrashlyticsBuildtools"); }

        /**
         * Version alias <b>firebaseDatabase</b> with value <b>21.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseDatabase() { return getVersion("firebaseDatabase"); }

        /**
         * Version alias <b>firebaseFirestore</b> with value <b>25.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseFirestore() { return getVersion("firebaseFirestore"); }

        /**
         * Version alias <b>firebaseMessaging</b> with value <b>24.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseMessaging() { return getVersion("firebaseMessaging"); }

        /**
         * Version alias <b>firebaseStorage</b> with value <b>21.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseStorage() { return getVersion("firebaseStorage"); }

        /**
         * Version alias <b>firebaseUiDatabase</b> with value <b>8.0.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFirebaseUiDatabase() { return getVersion("firebaseUiDatabase"); }

        /**
         * Version alias <b>fragment</b> with value <b>1.8.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFragment() { return getVersion("fragment"); }

        /**
         * Version alias <b>googleGmsGoogleServices</b> with value <b>4.4.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGoogleGmsGoogleServices() { return getVersion("googleGmsGoogleServices"); }

        /**
         * Version alias <b>googleServices</b> with value <b>4.4.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGoogleServices() { return getVersion("googleServices"); }

        /**
         * Version alias <b>gson</b> with value <b>2.10.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGson() { return getVersion("gson"); }

        /**
         * Version alias <b>json</b> with value <b>20210307</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJson() { return getVersion("json"); }

        /**
         * Version alias <b>junit</b> with value <b>4.13.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("junit"); }

        /**
         * Version alias <b>junitVersion</b> with value <b>1.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunitVersion() { return getVersion("junitVersion"); }

        /**
         * Version alias <b>material</b> with value <b>1.12.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMaterial() { return getVersion("material"); }

        /**
         * Version alias <b>navigationFragment</b> with value <b>2.7.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getNavigationFragment() { return getVersion("navigationFragment"); }

        /**
         * Version alias <b>navigationUi</b> with value <b>2.7.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getNavigationUi() { return getVersion("navigationUi"); }

        /**
         * Version alias <b>okhttp</b> with value <b>4.12.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getOkhttp() { return getVersion("okhttp"); }

        /**
         * Version alias <b>okhttpLoggingInterceptor</b> with value <b>5.0.0-alpha.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getOkhttpLoggingInterceptor() { return getVersion("okhttpLoggingInterceptor"); }

        /**
         * Version alias <b>picasso</b> with value <b>2.71828</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPicasso() { return getVersion("picasso"); }

        /**
         * Version alias <b>playServicesAuth</b> with value <b>21.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPlayServicesAuth() { return getVersion("playServicesAuth"); }

        /**
         * Version alias <b>preference</b> with value <b>1.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPreference() { return getVersion("preference"); }

        /**
         * Version alias <b>retrofit</b> with value <b>2.9.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRetrofit() { return getVersion("retrofit"); }

        /**
         * Version alias <b>retrofitConverterGson</b> with value <b>2.9.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRetrofitConverterGson() { return getVersion("retrofitConverterGson"); }

        /**
         * Version alias <b>volley</b> with value <b>1.2.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getVolley() { return getVersion("volley"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>androidApplication</b> with plugin id <b>com.android.application</b> and
         * with version reference <b>agp</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getAndroidApplication() { return createPlugin("androidApplication"); }

        /**
         * Plugin provider for <b>googleGmsGoogleServices</b> with plugin id <b>com.google.gms.google-services</b> and
         * with version reference <b>googleGmsGoogleServices</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getGoogleGmsGoogleServices() { return createPlugin("googleGmsGoogleServices"); }

    }

}
