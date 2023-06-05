package com.cuttlesystems.cuttlewallet.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cuttlesystems.cuttlewallet.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    /*
    private void initTableBalancesPreview()
    {
        Bundle bundle = this.getArguments();
        List<String> items = (ArrayList<String>) bundle.getStringArrayList("BalancesUser");
        List<String> itemsName = (ArrayList<String>) bundle.getStringArrayList("BalancesName");
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.table_balances_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        BalanceAdapter adapter = new BalanceAdapter(this.getContext(),items,itemsName);
        recyclerView.setAdapter(adapter);
    }

    private void initCustomizad(){
        View root = binding.getRoot();

        ImageView moreActionsIcon = (ImageView)root.findViewById(R.id.cryptoIcon);
        moreActionsIcon.setBackgroundResource(R.drawable.btc_icon);

        ImageView cryptoIcon = (ImageView)root.findViewById(R.id.moreActions);
        cryptoIcon.setBackgroundResource(R.drawable.more_actions_icon);

        TextView balanceEdit = (TextView)root.findViewById(R.id.BalanceEdit);
        balanceEdit.setTextColor(ContextCompat.getColor(this.getContext(), R.color.apricot));
        Bundle bundle = this.getArguments();
        balanceEdit.setText(bundle.getString("Balance"));

        TextView viewAll = (TextView) root.findViewById(R.id.balancesActivityStarted);
        SpannableString content = new SpannableString("View all");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        viewAll.setText(content);
    }
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}