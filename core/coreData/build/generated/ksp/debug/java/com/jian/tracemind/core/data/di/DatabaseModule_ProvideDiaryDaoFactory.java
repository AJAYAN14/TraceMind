package com.jian.tracemind.core.data.di;

import com.jian.tracemind.core.data.local.TraceMindDatabase;
import com.jian.tracemind.core.data.local.dao.DiaryDao;
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
public final class DatabaseModule_ProvideDiaryDaoFactory implements Factory<DiaryDao> {
  private final Provider<TraceMindDatabase> databaseProvider;

  private DatabaseModule_ProvideDiaryDaoFactory(Provider<TraceMindDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DiaryDao get() {
    return provideDiaryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDiaryDaoFactory create(
      Provider<TraceMindDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDiaryDaoFactory(databaseProvider);
  }

  public static DiaryDao provideDiaryDao(TraceMindDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDiaryDao(database));
  }
}
