package com.newmview.wifi.other;

import java.util.ArrayList;
import java.util.HashMap;

public class NeighboringCellsInfo{
    public static HashMap<Integer,Integer> lte_neighborCells;
    public static ArrayList<HashMap<Integer,Integer>>
            neighboringCellList=new ArrayList<>();
    public static ArrayList<String> lteParams;
    public static HashMap<Integer, String> nr_neighborCells;
    public static ArrayList<String> nrParams;
    public static ArrayList<HashMap<Integer, String>> nr_neighboringCellList;


    public static HashMap<Integer,Integer> wcdma_neighborCells;
    public static ArrayList<HashMap<Integer,Integer>> wcdma_neighboringCellList;
    public static ArrayList<HashMap<String, String>> neighboringCellList1=new ArrayList<HashMap<String, String>>();

    public static ArrayList<String> wcdmaParams;
    public static ArrayList<HashMap<Integer,Integer>>  gsm_neighboringCellList;
    public static ArrayList<String> gsmParams;
    public static int lte_neighbor_ss;
    public static int threeG_neighbor_ss;
    public static int gsm_neighnor_ss;




}
