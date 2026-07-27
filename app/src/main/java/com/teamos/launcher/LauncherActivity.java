package com.teamos.launcher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.teamos.launcher.LauncherDatabaseHelper.DbItem;
import com.teamos.launcher.LauncherModel.AppInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LauncherActivity extends AppCompatActivity implements LauncherModel.Callbacks {

    private SharedPreferences mPrefs;
    private LauncherModel mModel;
    private LauncherDatabaseHelper mDbHelper;

    private Workspace mWorkspace;
    private LinearLayout mPageIndicatorContainer;
    private LinearLayout mHotseatAppsContainer;
    private View mAllAppsPanel;
    private RecyclerView mAppsRecycler;
    private EditText mSearchInput;
    private ImageView mBtnChatGusto;

    private List<AppInfo> mAllAppsList = new ArrayList<>();
    private List<AppInfo> mFilteredAppsList = new ArrayList<>();
    private AppsAdapter mAppsAdapter;

    private String mIconShape;
    private boolean mGlowEffect;
    private int mGridCols = 4;
    private int mGridRows = 5;

    private static final String PREFS_NAME = "aero_launcher_prefs";

    // Gesture detector for home screen
    private GestureDetector mGestureDetector;

    // Battery status receiver
    private BroadcastReceiver mBatteryReceiver;
    private TextView mWidgetBatteryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        applyThemeSetting();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mModel = new LauncherModel();
        mDbHelper = new LauncherDatabaseHelper(this);

        mWorkspace = findViewById(R.id.workspace);
        mPageIndicatorContainer = findViewById(R.id.page_indicator);
        mHotseatAppsContainer = findViewById(R.id.hotseat_apps);
        mAllAppsPanel = findViewById(R.id.all_apps_panel);
        mAppsRecycler = findViewById(R.id.apps_recycler);
        mSearchInput = findViewById(R.id.search_input);
        mBtnChatGusto = findViewById(R.id.btn_chatgusto);

        // Load visual settings
        loadPreferences();

        // Setup Workspace with 2 pages
        setupWorkspace();

        // Setup All Apps drawer recycler view
        mAppsAdapter = new AppsAdapter();
        mAppsRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        mAppsRecycler.setAdapter(mAppsAdapter);

        // Load apps asynchronously
        mModel.loadAppsAsync(this, this);
        mModel.registerPackageReceiver(this, this);

        // Setup search filter
        setupSearch();

        // Setup gestures and clicks
        setupInteractions();

        // Register battery receiver for our custom TeamOS Widget
        registerBatteryReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if visual settings changed in SettingsActivity
        String oldShape = mIconShape;
        boolean oldGlow = mGlowEffect;
        int oldCols = mGridCols;
        int oldRows = mGridRows;

        loadPreferences();

        if (!mIconShape.equals(oldShape) || mGlowEffect != oldGlow || mGridCols != oldCols || mGridRows != oldRows) {
            // Reapply visual settings to hotseat and workspace
            rebuildWorkspaceLayout();
            if (mAppsAdapter != null) {
                mAppsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.unregisterPackageReceiver(this);
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);
        }
    }

    private void applyThemeSetting() {
        String theme = mPrefs.getString("theme", "dark");
        if ("light".equals(theme)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("dark".equals(theme)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void loadPreferences() {
        mIconShape = mPrefs.getString("icon_shape", "hexagon");
        mGlowEffect = mPrefs.getBoolean("glow_effect", true);

        String grid = mPrefs.getString("grid_size", "4x5");
        if ("5x5".equals(grid)) {
            mGridCols = 5;
            mGridRows = 5;
        } else if ("5x6".equals(grid)) {
            mGridCols = 5;
            mGridRows = 6;
        } else {
            mGridCols = 4;
            mGridRows = 5;
        }
    }

    private void setupWorkspace() {
        // Create 2 pages
        for (int i = 0; i < 2; i++) {
            CellLayout page = new CellLayout(this);
            page.initGrid(mGridCols, mGridRows);
            mWorkspace.addView(page);
        }

        // Setup page indicators
        mPageIndicatorContainer.removeAllViews();
        for (int i = 0; i < 2; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(16, 16);
            lp.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundColor(Color.parseColor("#00F0FF"));
            dot.setAlpha(i == 0 ? 1.0f : 0.4f);
            mPageIndicatorContainer.addView(dot);
        }

        mWorkspace.setOnPageChangeListener(new Workspace.OnPageChangeListener() {
            @Override
            public void onPageChanged(int currentPage) {
                for (int i = 0; i < mPageIndicatorContainer.getChildCount(); i++) {
                    mPageIndicatorContainer.getChildAt(i).setAlpha(i == currentPage ? 1.0f : 0.4f);
                }
            }
        });
    }

    private void registerBatteryReceiver() {
        mBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int pct = (int) (level * 100 / (float) scale);
                if (mWidgetBatteryText != null) {
                    mWidgetBatteryText.setText("Bateria: " + pct + "%");
                }
            }
        };
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void rebuildWorkspaceLayout() {
        // Clear all CellLayout pages
        for (int i = 0; i < mWorkspace.getChildCount(); i++) {
            CellLayout page = (CellLayout) mWorkspace.getChildAt(i);
            page.removeAllViews();
            page.initGrid(mGridCols, mGridRows);
            page.clearOccupiedCells();
        }
        mHotseatAppsContainer.removeAllViews();

        bindWorkspaceItems();
    }

    @Override
    public void onAppsLoaded(List<AppInfo> apps) {
        mAllAppsList = apps;
        mFilteredAppsList = new ArrayList<>(apps);
        mAppsAdapter.notifyDataSetChanged();

        // Build workspace layout
        rebuildWorkspaceLayout();
    }

    private void bindWorkspaceItems() {
        List<DbItem> savedItems = mDbHelper.getAllItems();

        if (savedItems.isEmpty()) {
            // First run: let's populate some defaults!
            populateDefaultWorkspace();
            savedItems = mDbHelper.getAllItems();
        }

        // Always add our beautiful built-in TeamOS Widget on Screen 0, at cell (0, 0) spanning (mGridCols, 2)
        addTeamOSWidget(0);

        for (DbItem item : savedItems) {
            AppInfo app = findAppByPackage(item.packageName);
            if (app != null) {
                if (item.screen == -1) {
                    // Hotseat shortcut
                    addHotseatShortcut(app);
                } else {
                    // Desktop screen shortcut
                    addDesktopShortcut(item.id, app, item.screen, item.cellX, item.cellY);
                }
            }
        }
    }

    private void populateDefaultWorkspace() {
        // Add 3 default apps to hotseat: Browser/Firefox, Contacts, Camera, Settings
        // We look for them in our loaded app list or standard ones
        int hotseatCount = 0;
        for (AppInfo app : mAllAppsList) {
            if (app.packageName.contains("chrome") || app.packageName.contains("browser") || app.packageName.contains("firefox")) {
                mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, -1, -1, -1, 1, 1));
                hotseatCount++;
            } else if (app.packageName.contains("setting")) {
                mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, -1, -1, -1, 1, 1));
                hotseatCount++;
            } else if (app.packageName.contains("contacts") || app.packageName.contains("dialer") || app.packageName.contains("phone")) {
                mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, -1, -1, -1, 1, 1));
                hotseatCount++;
            }
            if (hotseatCount >= 4) break;
        }

        // If hotseat is empty, just add first 4 loaded apps
        if (hotseatCount == 0 && !mAllAppsList.isEmpty()) {
            for (int i = 0; i < Math.min(4, mAllAppsList.size()); i++) {
                AppInfo app = mAllAppsList.get(i);
                mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, -1, -1, -1, 1, 1));
            }
        }

        // Add some default apps on Page 0 and Page 1
        int deskCount = 0;
        for (AppInfo app : mAllAppsList) {
            if (deskCount >= 4) break;
            // Let's place them on screen 0, row 2 (leaving room for our clock widget on row 0 & 1)
            int cellX = deskCount % mGridCols;
            int cellY = 2 + (deskCount / mGridCols);
            mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, 0, cellX, cellY, 1, 1));
            deskCount++;
        }
    }

    private AppInfo findAppByPackage(String packageName) {
        for (AppInfo app : mAllAppsList) {
            if (app.packageName.equals(packageName)) {
                return app;
            }
        }
        return null;
    }

    private void addTeamOSWidget(int screen) {
        CellLayout layout = mWorkspace.getCellLayoutAt(screen);
        if (layout == null) return;

        // Create custom Glassmorphic clock/battery widget programmatically
        CardView widgetView = new CardView(this);
        widgetView.setRadius(48f); // 48px corner radius
        widgetView.setCardBackgroundColor(Color.parseColor("#C0121620"));
        widgetView.setCardElevation(8f);

        // Customize the layout inside the widget
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setGravity(android.view.Gravity.CENTER);
        inner.setPadding(24, 24, 24, 24);

        TextView clockText = new TextView(this);
        clockText.setTextSize(32);
        clockText.setTextColor(Color.parseColor("#00F0FF"));
        clockText.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        clockText.setText(sdf.format(new Date()));

        // Update clock dynamically
        clockText.postDelayed(new Runnable() {
            @Override
            public void run() {
                clockText.setText(sdf.format(new Date()));
                clockText.postDelayed(this, 10000);
            }
        }, 10000);

        TextView dateText = new TextView(this);
        dateText.setTextSize(12);
        dateText.setTextColor(Color.WHITE);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("pt", "BR"));
        dateText.setText(sdfDate.format(new Date()));

        mWidgetBatteryText = new TextView(this);
        mWidgetBatteryText.setTextSize(11);
        mWidgetBatteryText.setTextColor(Color.parseColor("#D000FF"));
        mWidgetBatteryText.setPadding(0, 8, 0, 0);
        mWidgetBatteryText.setText("Bateria: --%");

        inner.addView(clockText);
        inner.addView(dateText);
        inner.addView(mWidgetBatteryText);

        widgetView.addView(inner);

        // Mark grid cells as occupied: spans full columns, rows 0 & 1
        layout.markCells(0, 0, mGridCols, 2, true);

        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(0, 0, mGridCols, 2);
        layout.addView(widgetView, lp);
    }

    private void addDesktopShortcut(final long id, final AppInfo app, int screen, int cellX, int cellY) {
        CellLayout layout = mWorkspace.getCellLayoutAt(screen);
        if (layout == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        BubbleTextView bubble = (BubbleTextView) inflater.inflate(R.layout.item_app_icon, layout, false);
        bubble.applyAppInfo(app.label, app.icon, mIconShape, mGlowEffect);

        bubble.setOnClickListener(v -> launchApp(app));

        bubble.setOnLongClickListener(v -> {
            showShortcutMenu(id, app);
            return true;
        });

        // Occupy cells
        layout.markCells(cellX, cellY, 1, 1, true);

        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(cellX, cellY, 1, 1);
        layout.addView(bubble, lp);
    }

    private void addHotseatShortcut(final AppInfo app) {
        LayoutInflater inflater = LayoutInflater.from(this);
        BubbleTextView bubble = (BubbleTextView) inflater.inflate(R.layout.item_app_icon, mHotseatAppsContainer, false);

        // Custom hotseat params: 56dp x 56dp for standard, labels are removed or very small in hotseat
        bubble.applyAppInfo(app.label, app.icon, mIconShape, mGlowEffect);
        TextView label = bubble.findViewById(R.id.app_label);
        if (label != null) {
            label.setVisibility(View.GONE); // Hide labels inside hotseat dock for an ultra-clean visual!
        }

        bubble.setOnClickListener(v -> launchApp(app));
        bubble.setOnLongClickListener(v -> {
            showHotseatShortcutMenu(app);
            return true;
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        mHotseatAppsContainer.addView(bubble, lp);
    }

    private void launchApp(AppInfo app) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(app.packageName, app.className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o aplicativo: " + app.label, Toast.LENGTH_SHORT).show();
        }
    }

    private void showShortcutMenu(final long id, final AppInfo app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(app.label)
                .setItems(new String[]{getString(R.string.remove_shortcut)}, (dialog, which) -> {
                    if (which == 0) {
                        mDbHelper.removeItem(id);
                        rebuildWorkspaceLayout();
                        Toast.makeText(this, "Atalho removido", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showHotseatShortcutMenu(final AppInfo app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(app.label)
                .setItems(new String[]{"Remover do Dock"}, (dialog, which) -> {
                    if (which == 0) {
                        // Find DbItem ID
                        List<DbItem> items = mDbHelper.getAllItems();
                        for (DbItem item : items) {
                            if (item.screen == -1 && item.packageName.equals(app.packageName)) {
                                mDbHelper.removeItem(item.id);
                                break;
                            }
                        }
                        rebuildWorkspaceLayout();
                    }
                })
                .show();
    }

    private void setupSearch() {
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mBtnChatGusto.setOnClickListener(v -> {
            // Prompt Virtual Assistant ChatGusto dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
            builder.setTitle("ChatGusto AI Assistant")
                    .setMessage("Olá! Sou o ChatGusto 3.0, assistente virtual oficial do TeamOS. Como posso ajudar com seu dispositivo hoje?")
                    .setPositiveButton("Perguntar", (dialog, which) -> {
                        String query = mSearchInput.getText().toString().trim();
                        if (!query.isEmpty()) {
                            Toast.makeText(LauncherActivity.this, "ChatGusto processando: \"" + query + "\"", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LauncherActivity.this, "Digite algo no campo de busca para perguntar!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Voltar", null)
                    .show();
        });
    }

    private void filterApps(String query) {
        mFilteredAppsList.clear();
        if (query == null || query.isEmpty()) {
            mFilteredAppsList.addAll(mAllAppsList);
        } else {
            String lower = query.toLowerCase();
            for (AppInfo app : mAllAppsList) {
                if (app.label.toLowerCase().contains(lower)) {
                    mFilteredAppsList.add(app);
                }
            }
        }
        mAppsAdapter.notifyDataSetChanged();
    }

    private void setupInteractions() {
        // Double tap and swipe gestures
        SwipeGestureDetector.OnGestureListener gestureListener = new SwipeGestureDetector.OnGestureListener() {
            @Override
            public void onSwipeUp() {
                openAppDrawer();
            }

            @Override
            public void onSwipeDown() {
                Toast.makeText(LauncherActivity.this, "Notificações (Gestos)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleTap() {
                Intent intent = new Intent(LauncherActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        };

        mGestureDetector = new GestureDetector(this, new SwipeGestureDetector(gestureListener));

        mWorkspace.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));

        // Open settings button Click
        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Close drawer panel
        findViewById(R.id.btn_close_drawer).setOnClickListener(v -> closeAppDrawer());
    }

    private void openAppDrawer() {
        mAllAppsPanel.setVisibility(View.VISIBLE);
        mAllAppsPanel.setAlpha(0f);
        mAllAppsPanel.setTranslationY(mAllAppsPanel.getHeight() > 0 ? mAllAppsPanel.getHeight() : 1000);
        mAllAppsPanel.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();
        mSearchInput.setText("");
        mSearchInput.requestFocus();
    }

    private void closeAppDrawer() {
        mAllAppsPanel.animate()
                .alpha(0f)
                .translationY(mAllAppsPanel.getHeight() > 0 ? mAllAppsPanel.getHeight() : 1000)
                .setDuration(250)
                .withEndAction(() -> mAllAppsPanel.setVisibility(View.GONE))
                .start();
    }

    @Override
    public void onBackPressed() {
        if (mAllAppsPanel.getVisibility() == View.VISIBLE) {
            closeAppDrawer();
        } else {
            // Do not exit on back press from home screen
        }
    }

    // Recycler view Adapter for all apps
    private class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            BubbleTextView view = (BubbleTextView) inflater.inflate(R.layout.item_app_icon, parent, false);
            return new AppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            final AppInfo app = mFilteredAppsList.get(position);
            holder.bubble.applyAppInfo(app.label, app.icon, mIconShape, mGlowEffect);

            // Light text color inside dark drawer background
            TextView labelText = holder.bubble.findViewById(R.id.app_label);
            if (labelText != null) {
                labelText.setTextColor(Color.WHITE);
                labelText.setVisibility(View.VISIBLE);
            }

            holder.bubble.setOnClickListener(v -> {
                closeAppDrawer();
                launchApp(app);
            });

            holder.bubble.setOnLongClickListener(v -> {
                // Long press gives option to add app shortcut to Workspace!
                AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
                builder.setTitle(app.label)
                        .setMessage("Deseja adicionar um atalho na Tela Inicial?")
                        .setPositiveButton("Adicionar", (dialog, which) -> {
                            closeAppDrawer();
                            // Find first vacant cell on current screen, else next screen
                            int currentScreen = mWorkspace.getCurrentPage();
                            CellLayout currentLayout = mWorkspace.getCellLayoutAt(currentScreen);
                            int[] cellXY = new int[2];
                            if (currentLayout != null && currentLayout.findVacantCell(cellXY, 1, 1)) {
                                mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, currentScreen, cellXY[0], cellXY[1], 1, 1));
                                rebuildWorkspaceLayout();
                                Toast.makeText(LauncherActivity.this, "Atalho adicionado à Tela Inicial", Toast.LENGTH_SHORT).show();
                            } else {
                                // Try next screen
                                int nextScreen = (currentScreen == 0) ? 1 : 0;
                                CellLayout nextLayout = mWorkspace.getCellLayoutAt(nextScreen);
                                if (nextLayout != null && nextLayout.findVacantCell(cellXY, 1, 1)) {
                                    mDbHelper.addItem(new DbItem(-1, app.packageName, app.className, 0, nextScreen, cellXY[0], cellXY[1], 1, 1));
                                    rebuildWorkspaceLayout();
                                    Toast.makeText(LauncherActivity.this, "Atalho adicionado à Tela Inicial", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LauncherActivity.this, "Não há espaço livre na Tela Inicial!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mFilteredAppsList.size();
        }

        class AppViewHolder extends RecyclerView.ViewHolder {
            BubbleTextView bubble;

            AppViewHolder(BubbleTextView view) {
                super(view);
                bubble = view;
            }
        }
    }
}
