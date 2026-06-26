package com.jian.tracemind.core.data.di;

import com.jian.tracemind.core.data.local.TraceMindDatabase;
import com.jian.tracemind.core.data.local.dao.FolderDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideFolderDaoFactory implements Factory<FolderDao> {
  private final Provider<TraceMindDatabase> databaseProvider;

  private DatabaseModule_ProvideFolderDaoFactory(Provider<TraceMindDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FolderDao get() {
    return provideFolderDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFolderDaoFactory create(
      Provider<TraceMindDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFolderDaoFactory(databaseProvider);
  }

  public static FolderDao provideFolderDao(TraceMindDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFolderDao(database));
  }
}
