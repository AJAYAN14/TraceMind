package com.jian.tracemind.feature.insights.ui;

import com.jian.tracemind.core.domain.repository.DiaryRepository;
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
public final class InsightsViewModel_Factory implements Factory<InsightsViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private InsightsViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public InsightsViewModel get() {
    return newInstance(diaryRepositoryProvider.get());
  }

  public static InsightsViewModel_Factory create(
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new InsightsViewModel_Factory(diaryRepositoryProvider);
  }

  public static InsightsViewModel newInstance(DiaryRepository diaryRepository) {
    return new InsightsViewModel(diaryRepository);
  }
}
