package com.cuttlesystems.cuttlewallet.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.util.ApiMessageException;
import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.BalanceAdapter;
import com.cuttlesystems.util.BalanceJsonParser;
import com.cuttlesystems.util.BalancesViewItemDecoration;
import com.cuttlesystems.util.Coins;
import com.cuttlesystems.util.CoinsJsonParser;
import com.cuttlesystems.util.SeedPhraseJsonParser;
import com.cuttlesystems.util.SelectedListener;
import com.cuttlesystems.util.SelectedListenerStory;
import com.cuttlesystems.util.StoryzAdapter;
import com.cuttlesystems.util.TotalBalance;
import com.cuttlesystems.util.UserBalance;
import com.cuttlesystems.cuttlewallet.R;
import com.cuttlesystems.cuttlewallet.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SelectedListener listenerBalance;
    private SelectedListenerStory listenerStory;
// -------------------------------------------------------------------------------------------------
    TextView totalView;
    TextView emptyMessage;
    TextView addAssetButton;
    ImageButton showSeedButton;
    ImageButton settingsButton;
    RecyclerView recycler;
    boolean stateLoading = false;

// -------------------------------------------------------------------------------------------------
    ArrayList<Coins> allCoins;
    ArrayList<UserBalance> balancesUser;
    BalanceAdapter adapter;
    TotalBalance total;
    Boolean seedState = false;
// -------------------------------------------------------------------------------------------------
    ApiMethodsProxy apiMethodsProxy;
// -------------------------------------------------------------------------------------------------
    String keyUser;
    String tokenUser;
// -------------------------------------------------------------------------------------------------
    private DataTransferListener mListener;
// -------------------------------------------------------------------------------------------------

    public interface DataTransferListener {
        void onDataTransfer(ArrayList<Coins> data);

    }

    class AssetsAsync extends android.os.AsyncTask<Void, Void, Void> {

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                apiMethodsProxy.getCoinsPost(tokenUser);
                CoinsJsonParser parser = new CoinsJsonParser(apiMethodsProxy.getCoins());
                allCoins = parser.getCoins();
            } catch (RuntimeException e) {
                Log.e("Runtime exception in HomeFragment",
                        "Runtime in apiMethodsProxy.getCoinsPost(tokenUser): " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Runtime exception in HomeFragment",
                        "JSON not parsed in parser.getCoins(): " + e.getMessage());
            } catch (Exception ex) {
                Log.e("Critical Error in HomeFragment", ex.getMessage());
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            addAssetButton.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("addAssetButton ", String.valueOf(addAssetButton));
            transferDataInMain();
            addAssetButton.setEnabled(true);
        }
    }

    class BalancesAsync extends android.os.AsyncTask<String, Void, JSONObject> {
        Handler handler = new Handler();
        Runnable updateTextRunnable;

        @SuppressLint({"LongLogTag", "SetTextI18n"})
        @Override
        protected JSONObject doInBackground(String... arrayLists) {
            try {
                apiMethodsProxy.getBalancesPost(arrayLists[0].toString(),
                        arrayLists[1].toString());
                JSONObject BalancesJson = apiMethodsProxy.getBalances();
                Log.d("Balances:",BalancesJson.toString());
                return BalancesJson;
            } catch (ApiMessageException ex){
                Log.d("Normal reaction on empty balances list from HomeFragment", ex.getMessage());
                return new JSONObject();
            }
            catch (Exception e) {
                Log.e("error", "Unexpected error: " +
                        e.getMessage());
                balancesUser = new ArrayList<>();
                return null;
            }
        }

        @SuppressLint({"LongLogTag", "SetTextI18n"})
        @Override
        protected void onPostExecute(JSONObject s){
            stateLoading = false;
            emptyMessage.setOnClickListener(null);

            handler.removeCallbacks(updateTextRunnable);
            if (seedState) {
                try {
                    BalanceJsonParser parser = new BalanceJsonParser(s);
                    balancesUser = parser.getBalances();
                    total = parser.getTotal();
                    if (balancesUser.isEmpty())
                        balancesUser = new ArrayList<UserBalance>();
                    if (total != null)
                        totalView.setText(total.getBalanceBTC() + " BTC");
                    else
                        totalView.setText("");
                    updateRecyclerView(balancesUser);
                } catch (JSONException e) {
                    Log.e("Error from HomeFragment", e.getMessage());
                    emptyMessage.setText("You may add balances clicked on\nADD ASSET");
                    totalView.setText("");
                    balancesUser = new ArrayList<UserBalance>();
                    updateRecyclerView(balancesUser);
                } catch (Exception ex) {
                    String text = "<font color=#979797>Connected error.</font>" +
                            "<font color=#1C1A18>Reconnected</font>";
                    emptyMessage.setText(Html.fromHtml(text));
                    totalView.setText("");
                    emptyMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Error from HomeFragment", "updated");
                            updateAll();
                        }});
                    Log.e("Critical error from HomeFragment", ex.getMessage());
                }
            }
            else {
                totalView.setText("");
                emptyMessage.setText("In order to start working with the wallet," +
                    "\nyou need to create a seed phrase");
            }
        }

        @Override
        protected void onPreExecute() {

            stateLoading = true;
            final String originalText = "Loading";
            final int ellipsesCount = 3;
            final int delayInMillis = 500;
            balancesUser = new ArrayList<UserBalance>();
            updateRecyclerView(balancesUser);

            updateTextRunnable = new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder(originalText);
                    for (int i = 0; i < count; i++) {
                        sb.append('.');
                    }
                    emptyMessage.setText(sb.toString());
                    totalView.setText(sb.toString());
                    count = (count + 1) % (ellipsesCount + 1);
                    handler.postDelayed(this, delayInMillis);
                }
            };

            handler.post(updateTextRunnable);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiMethodsProxy = new ApiMethodsProxy(root.getContext());

        emptyMessage = binding.getRoot().findViewById(R.id.empty_message);
        totalView = binding.getRoot().findViewById(R.id.totalBalance);
        addAssetButton = binding.getRoot().findViewById(R.id.add_asset_button);
        recycler = binding.getRoot().findViewById(R.id.user_balances_table);
        showSeedButton = binding.getRoot().findViewById(R.id.showSeed);
        settingsButton = binding.getRoot().findViewById(R.id.settings_button_home);

        settingsButton.setImageResource(R.drawable.settings_icon);
        settingsButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

        addAssetButton.setEnabled(false);
        showSeedButton.setImageResource(R.drawable.seed_icon);
        showSeedButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

        initTableStoryzPreview();

        try {
            Bundle arguments = getArguments();
            keyUser = arguments.getString("key");
            tokenUser = arguments.getString("token");

            try {
                apiMethodsProxy.getSeedPhrasesPost(tokenUser, keyUser);
            } catch (RuntimeException ex) {
                seedState = false;
                Log.e("Runtime exception in HomeFragment",
                        "Runtime in apiMethodsProxy.getSeedPhrasesPost(tokenUser): "
                                + ex.getMessage());
            } catch (Exception ex) {
                seedState = false;
                Log.e("Critical Error in HomeFragment",
                        ex.getMessage());
            }

            JSONObject seeds = apiMethodsProxy.getSeedPhrasesUser();
            SeedPhraseJsonParser seedParser = null;
            try {
                seedParser = new SeedPhraseJsonParser(seeds);
            } catch (JSONException e) {
                seedState = false;
                Log.e("Error from MainActivity", "Json with seeds is not created" + e.getMessage());
            }
            try {
                seedState = !seedParser.getSeed().isEmpty();
            } catch (JSONException e) {
                Log.e("Error from MainActivity", "Json with seeds is not created " +
                        "\nand not created ArrayList SeedPhrases " + e.getMessage() );
                seedState = false;
            }

            Log.d("seedState in HomeFragment",seedState.toString());
            if (seedState) {
                updateAll();
            }
            else {
                addAssetButton.setEnabled(false);
                emptyMessage.setText("In order to start working with the wallet, \nyou need to create a seed phrase");
            }
        } catch (Exception e) {
            Log.e("error", "Null data in transfer: " +
                    e.getMessage());
        }
        finally {
            Log.d("Log from HomeFragment", "Finish init data in HomeFragment");
        }

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DataTransferListener) {
            mListener = (DataTransferListener) context;
        } else {
            Log.e("Error from HomeFragment", "ClassCastException:not instead " +
                    "data transfer Listener in HomeFragment");
            throw new ClassCastException(context.toString() + " must implement DataTransferListener");
        }
        try {//
            listenerBalance = (SelectedListener) context;
        } catch (ClassCastException e) {
            Log.e("Error from HomeFragment", "ClassCastException:not instead " +
                    "Selected Listener in RecyclerView");
            throw new ClassCastException(context.toString() + " must implement SelectedListener");
        }
        finally
        {
            listenerStory = (SelectedListenerStory) context;
        }
    }

    private void initTableStoryzPreview()
    {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);

        RecyclerView StoryzTable =  binding.getRoot().findViewById(R.id.storyzTable);
        StoryzTable.setLayoutManager(layoutManager);
        StoryzAdapter adapter = new StoryzAdapter(this.getContext(), listenerStory);
        StoryzTable.setAdapter(adapter);
    }

    public void updateRecyclerView(ArrayList<UserBalance> balances) {
        try {
            adapter = (BalanceAdapter) recycler.getAdapter();
            try{
                adapter.clearAll();
                adapter.updateAll(balances);
                if (balances.size()==0) emptyMessage.setVisibility(View.VISIBLE);
                else emptyMessage.setVisibility(View.INVISIBLE);
            }
            catch (Exception r){
                Log.d("Debug HomeFragment",r.getMessage().toString());
                adapter.updateAll(balances);
                if (balances.size()==0) emptyMessage.setVisibility(View.VISIBLE);
                else emptyMessage.setVisibility(View.INVISIBLE);
            }
        }
        catch(Exception ex)
        {
            recycler.addItemDecoration(new BalancesViewItemDecoration(
                    getResources().getDimensionPixelSize(R.dimen.spacing)
            ));
            recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
            if(balances.size()!=0) {
                ArrayList <Parcelable> balancesParcelabel = new ArrayList<>();
                for (UserBalance balance : balances) {
                    balancesParcelabel.add(balance);
                }
                adapter = new BalanceAdapter(this.getContext(), balancesParcelabel, listenerBalance);
                recycler.setAdapter(adapter);
                emptyMessage.setVisibility(View.INVISIBLE);
            }
            else initEmptyTables();
            Log.e("Error from HomeFragment", "Input Bindings is EMPTY");
        }
    }

    public void updateBalancesData()
    {
        new AssetsAsync().execute();
        new BalancesAsync().execute(tokenUser, keyUser);
    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    public void updateAll()
    {
        apiMethodsProxy = new ApiMethodsProxy(binding.getRoot().getContext());

        emptyMessage.setVisibility(View.VISIBLE);

        addAssetButton.setEnabled(false);
        showSeedButton.setEnabled(false);
        emptyMessage.setOnClickListener(null);
        try {
            Bundle arguments = getArguments();
            keyUser = arguments.getString("key");
            tokenUser = arguments.getString("token");
            apiMethodsProxy.getSeedPhrasesPost(tokenUser, keyUser);
            JSONObject seeds = apiMethodsProxy.getSeedPhrasesUser();
            SeedPhraseJsonParser seedParser = null;
            try {
                seedParser = new SeedPhraseJsonParser(seeds);
                try {
                    seedState = !seedParser.getSeed().isEmpty();
                } catch (JSONException e) {
                    seedState = false;
                    Log.e("Error from MainActivity", "Json with seeds is not created " +
                            "\nand not created ArrayList SeedPhrases " + e.getMessage() );
                }
            } catch (RuntimeException ex) {
                seedState = false;
                Log.e("Runtime exception in HomeFragment",
                        "Runtime in apiMethodsProxy.getSeedPhrasesPost(tokenUser): "
                                + ex.getMessage());
            } catch (Exception ex) {
                seedState = false;
                Log.e("Critical Error in HomeFragment",
                        ex.getMessage());
            }

            Log.d("seedState in HomeFragment seedState in HomeFragment " +
                    "seedState in HomeFragment seedState in HomeFragment seedState in HomeFragment",
                    seedState.toString());

            if (seedState) {
                updateBalancesData();
            }
            else {
                adapter = (BalanceAdapter) recycler.getAdapter();
                seedState = false;

                if (adapter!=null) {
                    if (adapter.getItemCount() != 0) {
                        adapter.clearAll();
                    }
                }
                addAssetButton.setEnabled(false);
                emptyMessage.setText("In order to start working with the wallet, " +
                        "\nyou need to create a seed phrase");
                emptyMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAll();
                    }});
            }
        } catch (Exception e) {
            Log.e("error", "Null data in transfer: " +
                    e.getMessage());
        }
        finally {
            showSeedButton.setEnabled(true);
            Log.d("Log from HomeFragment", "Finish init update in HomeFragment");
        }
    }

    private void initEmptyTables()
    {
        emptyMessage.setVisibility(View.VISIBLE);
    }
    private void sendDataToMainActivity(ArrayList<Coins> data) {
        mListener.onDataTransfer(data);
    }
    private void transferDataInMain() {
        sendDataToMainActivity(allCoins);
    }

// -------------------------------------------------------------------------------------------------
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}