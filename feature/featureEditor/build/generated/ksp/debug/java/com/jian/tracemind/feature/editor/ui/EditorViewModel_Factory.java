package com.jian.tracemind.feature.editor.ui;

import androidx.lifecycle.SavedStateHandle;
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
public final class EditorViewModel_Factory implements Factory<EditorViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private EditorViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public EditorViewModel get() {
    return newInstance(savedStateHandleProvider.get(), diaryRepositoryProvider.get());
  }

  public static EditorViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new EditorViewModel_Factory(savedStateHandleProvider, diaryRepositoryProvider);
  }

  public static EditorViewModel newInstance(SavedStateHandle savedStateHandle,
      DiaryRepository diaryRepository) {
    return new EditorViewModel(savedStateHandle, diaryRepository);
  }
}
