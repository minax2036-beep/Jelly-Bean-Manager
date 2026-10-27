package jp.minax2036.jellybeanmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final int BG=0xFF0E121C, PANEL=0xFF191F2D, TEXT=0xFFEEF2FA, MUTED=0xFF9DAAC1, ACCENT=0xFF6991FF;
    private static final String[] TABS={"ホーム","端末","コンテンツ","プレイリスト","予定"};
    private LinearLayout body, nav;
    private TextView pageTitle, pageSubtitle;
    private String currentTab="ホーム";
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        prefs=getSharedPreferences("manager_data",MODE_PRIVATE);
        seedIfEmpty();
        buildShell();
        showPage("ホーム");
    }

    private JSONObject item(String name,String detail,String status) {
        JSONObject o=new JSONObject();
        try { o.put("name",name); o.put("detail",detail); o.put("status",status); } catch(Exception ignored) {}
        return o;
    }
    private JSONArray getItems(String key) {
        try { return new JSONArray(prefs.getString(key,"[]")); } catch(Exception e) { return new JSONArray(); }
    }
    private void saveItems(String key,JSONArray a) { prefs.edit().putString(key,a.toString()).apply(); }
    private void seedIfEmpty() {
        if(!prefs.contains("devices")) { JSONArray a=new JSONArray(); a.put(item("店頭ディスプレイ","入口 / Main Hall","未接続")); a.put(item("作業スペース","2F / Workspace","未接続")); saveItems("devices",a); }
        if(!prefs.contains("contents")) { JSONArray a=new JSONArray(); a.put(item("ようこそ","テキスト · 10秒","テキスト")); a.put(item("営業時間","画像 · 15秒","画像")); a.put(item("今日の予定","カレンダー · 20秒","カレンダー")); saveItems("contents",a); }
        if(!prefs.contains("playlists")) { JSONArray a=new JSONArray(); a.put(item("通常営業","3項目 · 45秒","有効")); saveItems("playlists",a); }
        if(!prefs.contains("schedules")) { JSONArray a=new JSONArray(); a.put(item("平日 営業時間","月〜金 · 09:00–18:00","有効")); saveItems("schedules",a); }
    }

    private void buildShell() {
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG); setContentView(root);
        LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.VERTICAL); top.setPadding(dp(22),dp(20),dp(22),dp(12));
        top.addView(text("JELLY BEAN  /  MANAGER",11,ACCENT,true));
        pageTitle=text("ホーム",28,TEXT,true); LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(-1,-2); tlp.topMargin=dp(8); top.addView(pageTitle,tlp);
        pageSubtitle=text("デジタルサイネージ管理",13,MUTED,false); top.addView(pageSubtitle); root.addView(top);
        ScrollView scroll=new ScrollView(this); body=new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(18),dp(8),dp(18),dp(24)); scroll.addView(body); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=new LinearLayout(this); nav.setGravity(Gravity.CENTER); nav.setPadding(dp(4),dp(8),dp(4),dp(8)); nav.setBackgroundColor(0xFF141925); root.addView(nav);
        for(String tab:TABS) { TextView b=text(tab,11,MUTED,false); b.setGravity(Gravity.CENTER); b.setPadding(dp(2),dp(13),dp(2),dp(13)); nav.addView(b,new LinearLayout.LayoutParams(0,-2,1)); b.setOnClickListener(v->showPage(tab)); }
    }
    private void showPage(String tab) {
        currentTab=tab; pageTitle.setText(tab); pageSubtitle.setText(subtitle(tab)); body.removeAllViews();
        if(tab.equals("ホーム")) dashboard();
        else if(tab.equals("端末")) listPage("devices","登録端末","端末を追加","端末名","設置場所");
        else if(tab.equals("コンテンツ")) contentPage();
        else if(tab.equals("プレイリスト")) listPage("playlists","プレイリスト","プレイリストを作成","名前","説明");
        else listPage("schedules","スケジュール","予定を追加","予定名","時間帯");
        for(int i=0;i<nav.getChildCount();i++) { TextView b=(TextView)nav.getChildAt(i); boolean selected=TABS[i].equals(tab); b.setTextColor(selected?ACCENT:MUTED); b.setTypeface(null,selected?Typeface.BOLD:Typeface.NORMAL); }
    }
    private String subtitle(String tab) {
        if(tab.equals("端末")) return "ディスプレイを登録して個別に管理";
        if(tab.equals("コンテンツ")) return "表示する素材を整理";
        if(tab.equals("プレイリスト")) return "表示内容と再生順を管理";
        if(tab.equals("予定")) return "時間に合わせた切り替えを設定";
        return "デジタルサイネージ管理";
    }

    private void dashboard() {
        section("概要");
        LinearLayout metrics=new LinearLayout(this); metrics.setOrientation(LinearLayout.HORIZONTAL);
        metrics.addView(metric("端末",String.valueOf(getItems("devices").length()),"登録済み"),new LinearLayout.LayoutParams(0,-2,1));
        metrics.addView(metric("素材",String.valueOf(getItems("contents").length()),"ローカル"),new LinearLayout.LayoutParams(0,-2,1));
        metrics.addView(metric("リスト",String.valueOf(getItems("playlists").length()),"作成済み"),new LinearLayout.LayoutParams(0,-2,1)); body.addView(metrics);
        section("クイックアクション");
        actionCard("＋ 端末を登録","管理するサイネージを追加",()->showPage("端末"));
        actionCard("▧  コンテンツを管理","画像・動画・テキストなど",()->showPage("コンテンツ"));
        actionCard("▶  プレイリストを編集","表示順と自動切り替え",()->showPage("プレイリスト"));
        section("登録端末"); JSONArray a=getItems("devices"); for(int i=0;i<Math.min(3,a.length());i++) renderItem("devices",i,a.optJSONObject(i));
        section("接続について"); cardNote("現在はローカル管理モードです。端末への送受信は未接続で、通信機能は最後の工程で実装します。");
    }
    private android.view.View metric(String label,String value,String hint) {
        LinearLayout b=cardBox(); b.setPadding(dp(12),dp(15),dp(8),dp(13)); b.addView(text(label,12,MUTED,false)); b.addView(text(value,27,TEXT,true)); b.addView(text(hint,10,MUTED,false)); return b;
    }
    private void contentPage() {
        section("コンテンツライブラリ");
        actionButton("＋ コンテンツを追加",()->addDialog("contents","コンテンツを追加","名前","詳細（例：テキスト · 10秒）","種類（テキスト / 画像 / 動画 / 天気 / カレンダー）"));
        JSONArray a=getItems("contents"); for(int i=0;i<a.length();i++) renderItem("contents",i,a.optJSONObject(i));
        cardNote("現在は素材の情報をローカル保存します。ファイル選択・プレビュー・実際の再生は次の実装段階です。");
    }
    private void listPage(String key,String heading,String button,String firstHint,String secondHint) {
        section(heading); actionButton("＋ "+button,()->addDialog(key,button,firstHint,secondHint,key.equals("devices")?"状態":"状態"));
        JSONArray a=getItems(key); if(a.length()==0) cardNote("まだ登録されていません。「"+button+"」から追加できます。");
        for(int i=0;i<a.length();i++) renderItem(key,i,a.optJSONObject(i));
        if(key.equals("devices")) { section("ペアリング"); cardNote("QRコード登録・LAN内の再発見・自動再接続は、通信機能の実装時に接続します。"); }
        else if(key.equals("playlists")) { section("再生ルール"); cardNote("現在はリスト名と説明を保存できます。項目の並べ替え・表示時間は次段階で実装します。"); }
        else { section("スケジュールについて"); cardNote("現在は予定名と時間帯を保存できます。曜日別設定と自動配信は後で実装します。"); }
    }
    private void renderItem(String key,int index,JSONObject obj) {
        if(obj==null)return;
        LinearLayout card=cardBox(); card.setPadding(dp(15),dp(12),dp(10),dp(12));
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL);
        info.addView(text(obj.optString("name","名称未設定"),15,TEXT,true));
        TextView detail=text(obj.optString("detail",""),12,MUTED,false); LinearLayout.LayoutParams dl=new LinearLayout.LayoutParams(-1,-2); dl.topMargin=dp(4); info.addView(detail,dl);
        row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        TextView edit=text("編集",12,ACCENT,true); edit.setPadding(dp(7),dp(10),dp(7),dp(10)); edit.setOnClickListener(v->editDialog(key,index,obj)); row.addView(edit);
        TextView del=text("削除",12,0xFFFF7E8B,true); del.setPadding(dp(7),dp(10),dp(4),dp(10)); del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("削除しますか？").setMessage("「"+obj.optString("name")+"」を削除します。").setNegativeButton("キャンセル",null).setPositiveButton("削除",(d,w)->{ JSONArray old=getItems(key), next=new JSONArray(); for(int j=0;j<old.length();j++)if(j!=index)next.put(old.opt(j)); saveItems(key,next); showPage(currentTab); }).show()); row.addView(del);
        card.addView(row); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=dp(9); body.addView(card,lp);
    }
    private void addDialog(String key,String title,String firstHint,String secondHint,String thirdHint) {
        LinearLayout form=form(); EditText name=field(firstHint), detail=field(secondHint), status=field(thirdHint); form.addView(name);form.addView(detail);form.addView(status);
        new AlertDialog.Builder(this).setTitle(title).setView(form).setNegativeButton("キャンセル",null).setPositiveButton("保存",(d,w)->{String n=name.getText().toString().trim();if(n.isEmpty())n="名称未設定";JSONArray a=getItems(key);a.put(item(n,detail.getText().toString().trim(),status.getText().toString().trim()));saveItems(key,a);showPage(currentTab);}).show();
    }
    private void editDialog(String key,int index,JSONObject old) {
        LinearLayout form=form(); EditText name=field("名前"),detail=field("詳細"),status=field("種類 / 状態");
        name.setText(old.optString("name",""));detail.setText(old.optString("detail",""));status.setText(old.optString("status",""));form.addView(name);form.addView(detail);form.addView(status);
        new AlertDialog.Builder(this).setTitle("編集").setView(form).setNegativeButton("キャンセル",null).setPositiveButton("保存",(d,w)->{JSONArray a=getItems(key);try{a.put(index,item(name.getText().toString().trim(),detail.getText().toString().trim(),status.getText().toString().trim()));}catch(org.json.JSONException e){Toast.makeText(this,"保存できませんでした",Toast.LENGTH_SHORT).show();return;}saveItems(key,a);showPage(currentTab);}).show();
    }
    private LinearLayout form(){LinearLayout f=new LinearLayout(this);f.setOrientation(LinearLayout.VERTICAL);f.setPadding(dp(20),dp(8),dp(20),0);return f;}
    private EditText field(String hint){EditText e=new EditText(this);e.setSingleLine(true);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setHint(hint);e.setTextSize(14);return e;}
    private void section(String title){TextView t=text(title,15,TEXT,true);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(14);lp.bottomMargin=dp(10);body.addView(t,lp);}
    private void actionCard(String title,String detail,Runnable action){LinearLayout c=cardBox();c.setPadding(dp(15),dp(14),dp(15),dp(14));c.addView(text(title,14,TEXT,true));TextView d=text(detail,12,MUTED,false);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(5);c.addView(d,lp);c.setOnClickListener(v->action.run());LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(-1,-2);clp.bottomMargin=dp(9);body.addView(c,clp);}
    private void actionButton(String label,Runnable action){TextView b=text(label,14,Color.WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(14),dp(14),dp(14),dp(14));b.setBackground(shape(ACCENT,12));b.setOnClickListener(v->action.run());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(8);body.addView(b,lp);}
    private void cardNote(String message){LinearLayout c=cardBox();c.setPadding(dp(15),dp(14),dp(15),dp(14));c.addView(text(message,13,MUTED,false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(9);body.addView(c,lp);}
    private LinearLayout cardBox(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setBackground(shape(PANEL,16));return c;}
    private TextView text(String value,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private int dp(int value){return (int)(value*getResources().getDisplayMetrics().density+0.5f);}
}
