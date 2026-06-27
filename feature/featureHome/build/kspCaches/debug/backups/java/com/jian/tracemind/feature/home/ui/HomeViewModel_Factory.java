package com.jian.tracemind.feature.home.ui;

import com.jian.tracemind.core.domain.repository.DiaryRepository;
import com.jian.tracemind.core.domain.repository.FolderRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<FolderRepository> folderRepositoryProvider;

  private HomeViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.folderRepositoryProvider = folderRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), folderRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider) {
    return new HomeViewModel_Factory(diaryRepositoryProvider, folderRepositoryProvider);
  }

  public static HomeViewModel newInstance(DiaryRepository diaryRepository,
      FolderRepository folderRepository) {
    return new HomeViewModel(diaryRepository, folderRepository);
  }
}
