package com.cuttlesystems.util;

import java.util.ArrayList;

public interface SelectedListener {
    void onMyItemClick(String name, String value, String Id, String code, String icon) throws Exception;
    void updateRecyclerView(ArrayList<UserBalance> balances);
}

