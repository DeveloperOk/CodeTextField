package com.enterprise.codetextfield

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    //Code TextField Number Input
    val numberOfDigits = 4
    val focusRequestersForNumberInput = List(numberOfDigits) { FocusRequester() }
    val digits = List<MutableState<String?>>(numberOfDigits) { mutableStateOf<String?>(null) }



    //Code TextField Character Input
    val numberOfCharacters = 5
    val focusRequestersForCharacterInput = List(numberOfCharacters) { FocusRequester() }
    val characters =
        List<MutableState<String?>>(numberOfCharacters) { mutableStateOf<String?>(null) }



}