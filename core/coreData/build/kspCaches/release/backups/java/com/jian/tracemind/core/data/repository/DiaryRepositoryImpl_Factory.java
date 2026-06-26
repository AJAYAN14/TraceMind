package com.jian.tracemind.core.data.repository;

import com.jian.tracemind.core.data.local.dao.DiaryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DiaryRepositoryImpl_Factory implements Factory<DiaryRepositoryImpl> {
  private final Provider<DiaryDao> diaryDaoProvider;

  private DiaryRepositoryImpl_Factory(Provider<DiaryDao> diaryDaoProvider) {
    this.diaryDaoProvider = diaryDaoProvider;
  }

  @Override
  public DiaryRepositoryImpl get() {
    return newInstance(diaryDaoProvider.get());
  }

  public static DiaryRepositoryImpl_Factory create(Provider<DiaryDao> diaryDaoProvider) {
    return new DiaryRepositoryImpl_Factory(diaryDaoProvider);
  }

  public static DiaryRepositoryImpl newInstance(DiaryDao diaryDao) {
    return new DiaryRepositoryImpl(diaryDao);
  }
}
