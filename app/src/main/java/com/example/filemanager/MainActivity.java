package com.example.filemanager;

import static android.content.ContentValues.TAG;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextAdapter adapter1;
    String currentPath;
    private boolean[] selection;
    private List<String> filesList;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private boolean isFileManagerInitialized = false;
    String rootPath;
    File dir;
    File[] files;
    int filesFoundCount;
    private static final String TAG = "PERMISSION_TAG";
    ListView lv;
    private boolean isLongClick;
    private int selected;
    private String copyPath;
    private boolean copyDone;
    private boolean pasteDone;
    private String pa,pass;
    int show_pass;
    String temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout1);
        if (!checkPermission()) {
            Intent i=new Intent(MainActivity.this,Sign_Up.class);
            startActivity(i);
            requestPermission();


        }
        Intent pat=getIntent();
        pass=pat.getStringExtra("Pass");
        show_pass=pat.getIntExtra("Ok",0);
        if(show_pass==123){
           Toast.makeText(this, "Correct Password "+pass, Toast.LENGTH_SHORT).show();
        }
        if(pat.getStringExtra("True")!=null){
            pa=pat.getStringExtra("True");
        }
        TextView pathOutput = findViewById(R.id.pathOutput);
        if (!isFileManagerInitialized) {
            currentPath=String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            rootPath=currentPath.substring(0,currentPath.lastIndexOf("/"));
currentPath=rootPath;
            dir = new File(currentPath);
            files = dir.listFiles();
temp=currentPath.substring(currentPath.lastIndexOf("/") + 1);
if(temp.equals("0")){
    pathOutput.setText("Home");
}else {
    pathOutput.setText(temp);
}
            filesFoundCount = files.length;

            isFileManagerInitialized = true;
        }
        lv = findViewById(R.id.listview);
        adapter1 = new TextAdapter();
        lv.setAdapter(adapter1);
        filesList = new ArrayList<>();
        for (int i = 0; i < filesFoundCount; i++) {
            filesList.add("FILE " + String.valueOf(files[i].getAbsolutePath()));
        }
        adapter1.setData(filesList);
        selection = new boolean[filesFoundCount];
        for (int i = 0; i < filesFoundCount; i++) {
            selection[i] = false;
        }
        Button Refresh=findViewById(R.id.refresh);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(copyDone&&!pasteDone){
    findViewById(R.id.buttonBar).setVisibility(View.VISIBLE);
}
if(pasteDone){
    findViewById(R.id.buttonBar).setVisibility(View.GONE);
    pasteDone=false;
}
                files=dir.listFiles();
                if(files==null)
                    return;
                filesFoundCount=files.length;
                filesList.clear();
                for (int p = 0; p < filesFoundCount; p++) {
                    filesList.add(String.valueOf(files[p].getAbsolutePath()));
                }
                selection = new boolean[filesFoundCount];
                for (int i = 0; i < filesFoundCount; i++) {
                    selection[i] = false;
                }
                adapter1.setSelection(selection);
                adapter1.setData(filesList);
            }
        });
        if(pa!=null){

                currentPath = pa;
                dir = new File(currentPath);
                pathOutput.setText(currentPath.substring(currentPath.lastIndexOf("/") + 1));
                Refresh.callOnClick();

        }
        TextView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(currentPath.equals(rootPath)){
                                            return;
                                        }
                                        currentPath=currentPath.substring(0,currentPath.lastIndexOf("/"));
                                        dir=new File(currentPath);
                                        pathOutput.setText(currentPath.substring(currentPath.lastIndexOf("/") + 1));
                                        Refresh.callOnClick();
                                    }
                                });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      if(!isLongClick){
                          if(position>files.length){
                              return;
                          }
                          if(files[position].isDirectory()){
                              String a=files[position].getAbsolutePath();
                              a=a.substring(a.lastIndexOf("/")+1);
                              Log.d("safe", a);
                              if(a.equals("Safe_folder")){
Intent s=new Intent(MainActivity.this,Verification.class);
s.putExtra("Path",files[position].getAbsolutePath().toString());
startActivity(s);
                    }
                              else {
                                  currentPath = files[position].getAbsolutePath();
                                  dir = new File(currentPath);
                                  pathOutput.setText(currentPath.substring(currentPath.lastIndexOf("/") + 1));
                                  findViewById(R.id.buttonBar).setVisibility(View.GONE);
                                  Refresh.callOnClick();
                              }
                          }
                      }
                  }
              },50);

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isLongClick=true;
                selection[position] = !selection[position];
                adapter1.setSelection(selection);


                int sel_Count=0;
                for(int i=0;i<selection.length;i++){
                    if(selection[i]) {
                        sel_Count++;
                        selected=i;
                    }
                }
                if (sel_Count>0) {

                    if(sel_Count==1)
                        findViewById(R.id.b2).setVisibility(View.VISIBLE);
                    else
                        findViewById(R.id.b2).setVisibility(View.GONE);
                    findViewById(R.id.buttonBar).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.buttonBar).setVisibility(View.GONE);

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongClick=false;
                    }

                },1000);
                return false;
            }
        });

       TextView b1 = findViewById(R.id.b1);
        TextView b2 = findViewById(R.id.b2);
        TextView b3 = findViewById(R.id.b3);
        TextView b4 = findViewById(R.id.b4);
        b1.setText("Delete");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.this);
                deleteDialog.setTitle("Delete");
                deleteDialog.setMessage("Are you sure you want to delete?");
                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < files.length; j++) {
                            if (selection[j]) {
                                deleteFileOrFolder(files[j]);
                                selection[j]=false;
                            }
                        }
                        Refresh.callOnClick();
                        findViewById(R.id.buttonBar).setVisibility(View.GONE);
                    }
                });
                deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteDialog.show();
                Log.d("deleted", "done");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder renameDialog=new AlertDialog.Builder(MainActivity.this);
                renameDialog.setTitle("Rename to");
                renameDialog.setMessage("Enter the new name:");
                final EditText rename=new EditText(MainActivity.this);
                String renamePath=files[selected].getAbsolutePath();

rename.setText( renamePath.substring(renamePath.lastIndexOf("/")+1));
rename.setInputType(InputType.TYPE_CLASS_TEXT);
renameDialog.setView(rename);
renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
   String s=new File(renamePath).getParent()+"/"+rename.getText();
   File newFile=new File(s);
   new File(renamePath).renameTo(newFile);
   selection[selected]=false;
        findViewById(R.id.buttonBar).setVisibility(View.GONE);
        Refresh.callOnClick();
    }
});
renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        selection[selected]=false;
        findViewById(R.id.buttonBar).setVisibility(View.GONE);
        Refresh.callOnClick();
        dialog.cancel();
    }
});
renameDialog.show();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyPath=files[selected].getAbsolutePath();
                b4.setVisibility(View.VISIBLE);
                copyDone=true;
                pasteDone=false;
                Refresh.callOnClick();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b4.setVisibility(View.GONE);
                String dst=currentPath+"/"+copyPath.substring(copyPath.lastIndexOf("/"));
                copy(new File(copyPath),new File(dst));
pasteDone=true;
copyDone=false;
                Refresh.callOnClick();
            }
        });
        Button createNewFolder=findViewById(R.id.newfolder);
        createNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newFolderDialog=new AlertDialog.Builder(MainActivity.this);
                newFolderDialog.setTitle("New Folder");
                newFolderDialog.setMessage("Enter the name of the new folder:");
                final EditText newFolderName=new EditText(MainActivity.this);
                newFolderName.setInputType(InputType.TYPE_CLASS_TEXT);
                newFolderDialog.setView(newFolderName);
                newFolderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File newFolder=new File(currentPath+"/"+newFolderName.getText());
                        if(!newFolder.exists()){
                            newFolder.mkdir();
                            Refresh.callOnClick();
                        }
                    }
                });
                newFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newFolderDialog.show();
            }
        });
    }



private void copy(File src,File dst){
    try {
        if(src.isDirectory()){
            if(!dst.exists()){
                dst.mkdir();
            }
            String files[]=src.list();
            for(String f:files){
                copy(new File(src,f),new File(dst,f));
            }
        }
        else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
    public void deleteFileOrFolder(File file) {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();
                for (String str : files) {
                    File fileDelete = new File(file, str);
                    deleteFileOrFolder(fileDelete);
                }
                if(file.list().length==0){
                    file.delete();
                }
            }
        }else{
            file.delete();
        }
    }
    class TextAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private boolean[] selection;
        public void setSelection(boolean[] selection){
            if(selection!=null){
                this.selection=new boolean[selection.length];
                for(int i=0;i<selection.length;i++){
                    this.selection[i]=selection[i];
                }
                notifyDataSetChanged();
            }
        }
        public void setData(List<String> data) {
            if (data != null) {
                this.data.clear();
                if (data.size() > 0) {
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                convertView.setTag(new viewholder((TextView) convertView.findViewById(R.id.textItem)));
            }
            viewholder vh = (viewholder) convertView.getTag();
            String item = getItem(position);
            vh.info.setText(item.substring(item.lastIndexOf("/") + 1));
            if(selection!=null){
                if(selection[position]){
                    vh.info.setBackgroundColor(getResources().getColor(R.color.lightColor));
                }
                else{
                    vh.info.setBackgroundColor(getResources().getColor(R.color.white));
                }

            }
            return convertView;
        }

        class viewholder {
            TextView info;

            viewholder(TextView i) {
                info = i;
            }
        }
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Log.d(TAG, "requestPermission: try");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.e(TAG, "requestPermission: " + e.toString());
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: called");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            Log.d(TAG, "onActivityResult: Permission granted");
                            // Permission granted, initialize app

                        } else {
                            Log.d(TAG, "onActivityResult: Permission denied");
                            // Permission denied, display message and finish activity
                            Toast.makeText(MainActivity.this, "Permission denied. App cannot run.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        // Handle permission result for lower Android versions
                        if (result.getResultCode() == RESULT_OK) {
                            Log.d(TAG, "onActivityResult: Permission granted");

                        } else {
                            Log.d(TAG, "onActivityResult: Permission denied");
                            Toast.makeText(MainActivity.this, "Permission denied. App cannot run.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
    );

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (write && read) {
                Log.d(TAG, "onRequestPermissionsResult: granted");
            } else {
                Log.d(TAG, "onRequestPermissionsResult: denied");


            }
        }
    }
}