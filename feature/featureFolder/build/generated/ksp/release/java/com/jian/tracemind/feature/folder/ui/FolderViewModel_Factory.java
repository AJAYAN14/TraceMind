package com.jian.tracemind.feature.folder.ui;

import androidx.lifecycle.SavedStateHandle;
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
public final class FolderViewModel_Factory implements Factory<FolderViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<FolderRepository> folderRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private FolderViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.folderRepositoryProvider = folderRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public FolderViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), folderRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static FolderViewModel_Factory create(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<FolderRepository> folderRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new FolderViewModel_Factory(diaryRepositoryProvider, folderRepositoryProvider, savedStateHandleProvider);
  }

  public static FolderViewModel newInstance(DiaryRepository diaryRepository,
      FolderRepository folderRepository, SavedStateHandle savedStateHandle) {
    return new FolderViewModel(diaryRepository, folderRepository, savedStateHandle);
  }
}
