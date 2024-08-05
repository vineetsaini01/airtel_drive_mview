package com.newmview.wifi.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mview.airtel.R;
import com.newmview.wifi.adapter.GraphsListAdapter;
import com.newmview.wifi.bean.Graphs_Bean;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;

public class Graphs_BottomsheetFragment extends BottomSheetDialogFragment implements View.OnClickListener  {
    private View view;
    private RecyclerView graphslist;
    private GraphsListAdapter graphsListAdapter;
    private ArrayList<Graphs_Bean> graphsList=new ArrayList<>();
    private DB_handler db_handler;
    private Bundle bundle;
    private int pos;
   private int screenheight;
    public static int bottomsheetheight;
    private AutoCompleteTextView searchforgraph;
    private ImageView cancelSearch;
    private View linearLayout;
    private TextView dismiss;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLayout;


    public Graphs_BottomsheetFragment()
    {

    }

   /* @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog d = super.onCreateDialog(savedInstanceState);
        // view hierarchy is inflated after dialog is shown
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //this disables outside touch
              //  d.getWindow().findViewById(R.id.touch_outside).setOnClickListener(null);
                //this prevents dragging behavior
                View content = d.getWindow().findViewById(R.id.design_bottom_sheet);
                ((CoordinatorLayout.LayoutParams) content.getLayoutParams()).setBehavior(null);

            }
        });
        return d;
    }

*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.graphs_bottomsheet,container,false);
        init();
     /*   bottomSheetBehavior=BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    ((BottomSheetBehavior) bottomSheetBehavior).setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
*/

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        graphslist.setLayoutManager(linearLayoutManager);
        fetchGraphsList();
        searchforgraph.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cancelSearch.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                graphsListAdapter.getFilter().filter(s);
                if(s.length()>0) {
                    cancelSearch.setVisibility(View.VISIBLE);
                }
                else {
                    cancelSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        return view;
    }

    private void init() {
        bundle=this.getArguments();
        bottomSheetLayout=view.findViewById(R.id.bottomsheetlayout);
        pos= bundle.getInt("dbposition");

        screenheight=Utils.getScreenHeight(getActivity());
        if( CommonUtil.mappgraphlist.get(pos).size()>=5) {
            bottomsheetheight = screenheight / 2;
        }
        else {
            bottomsheetheight = screenheight / 4;
        }
        System.out.println("screen height is "+screenheight + "and size is "+ CommonUtil.mappgraphlist.get(pos).size());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                bottomsheetheight));
        graphslist=view.findViewById(R.id.recyclerview);

        searchforgraph=view.findViewById(R.id.searchforgraph);
        cancelSearch=view.findViewById(R.id.crossimg);
        linearLayout=view.findViewById(R.id.searchlayout);
        dismiss=(TextView)view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(this);
        if(CommonUtil.mappgraphlist.get(pos).size()> Config.listSize)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
        else {
           linearLayout.setVisibility(View.GONE);
        }
        cancelSearch.setOnClickListener(this);

    }

    private void fetchGraphsList() {
if(CommonUtil.mappgraphlist!=null && CommonUtil.mappgraphlist.size()>0) {

    for (int i = 0; i < CommonUtil.mappgraphlist.get(pos).size(); i++) {
        String graphId = CommonUtil.mappgraphlist.get(pos).get(i).get("graphId");
        String graphName = CommonUtil.mappgraphlist.get(pos).get(i).get("graphName");
        Graphs_Bean graphs_bean = new Graphs_Bean();
        graphs_bean.setName(graphName);
        graphs_bean.setId(graphId);
        graphsList.add(graphs_bean);


        System.out.println("graph id and name is " + graphId + "   " + graphName);


    }
    graphsListAdapter = new GraphsListAdapter(graphsList, getActivity());
    graphslist.setAdapter(graphsListAdapter);
}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.dismiss:
              dismiss();
              break;
            case R.id.crossimg:
                searchforgraph.getText().clear();
                break;

        }
    }
}
