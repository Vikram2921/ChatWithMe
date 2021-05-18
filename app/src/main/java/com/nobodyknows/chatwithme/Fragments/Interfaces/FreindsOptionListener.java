package com.nobodyknows.chatwithme.Fragments.Interfaces;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;

import java.util.List;

public interface FreindsOptionListener {
    public void onConfirm(FreindRequestDTO freindRequestDTO);
    public void onDelete(FreindRequestDTO freindRequestDTO);
}
