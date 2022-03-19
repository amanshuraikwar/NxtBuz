package io.github.amanshuraikwar.testutil

import com.russhwolf.settings.MockSettings
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage

class FakePreferenceStorage(
    preferenceStorage: PreferenceStorage = PreferenceStorage.create(MockSettings())
) : PreferenceStorage by preferenceStorage