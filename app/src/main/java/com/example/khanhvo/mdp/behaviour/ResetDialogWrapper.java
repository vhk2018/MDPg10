package com.example.khanhvo.mdp.behaviour;

import com.example.khanhvo.mdp.enumType.ResetCode;


public interface ResetDialogWrapper {
    void doPositiveClick(ResetCode action);
    void doNegativeClick();
}
