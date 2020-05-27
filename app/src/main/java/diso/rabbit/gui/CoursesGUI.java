package diso.rabbit.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import diso.rabbit.R;
import diso.rabbit.data.Course;
import diso.rabbit.data.CourseDao;
import diso.rabbit.helper.Utils;
import diso.rabbit.model.CourseModel;

/**
 * Created by pabdiava on 16/04/2015.
 */
public class CoursesGUI extends Activity {

    // Vars
    CourseDao coursesDao;
    ArrayAdapter<CourseModel> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.courses);

        coursesDao = Utils.GetDaoSession(this).getCourseDao();
        ShowCourses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addCourse:
                ShowDialog(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual_base, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CourseModel course = (CourseModel)adapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.edit:
                ShowDialog(info.targetView.getContext(), course);
                return true;
            case R.id.delete:
                ConfirmDelete(info.targetView.getContext(), course);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void ShowCourses() {

        adapter = new ArrayAdapter<CourseModel>(this, android.R.layout.simple_list_item_1, Utils.GetCourses(coursesDao));

        ListView lstCourses = (ListView) findViewById(R.id.lstCourses);
        lstCourses.setAdapter(adapter);
        registerForContextMenu(lstCourses);

        lstCourses.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CourseModel course = (CourseModel)parent.getItemAtPosition(position);
                        ShowDialog(parent.getContext(), course);
                    }
                }
        );
    }

    void ShowDialog(Context context){
        ShowDialog(context, null);
    }

    void ShowDialog(Context context, final CourseModel course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.courses_edit, null);

        final EditText courseEdit = (EditText) dialogView.findViewById(R.id.course);
        if (course != null) { // Edit
            courseEdit.setText(course.getName());
            courseEdit.selectAll();
            courseEdit.requestFocus();
        }

        builder.setView(dialogView);

        builder.setTitle(context.getResources().getString(R.string.course2))
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HideKeyboard(courseEdit);
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });

        dialog.setCanceledOnTouchOutside(false); // Prevent Cancel outside dialog
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(course == null) {
                    if (AddCourse(courseEdit.getText().toString())) {
                        HideKeyboard(courseEdit);
                        dialog.dismiss();
                    }
                }
                else{
                    if (EditCourse(courseEdit.getText().toString(), course)) {
                        HideKeyboard(courseEdit);
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void HideKeyboard(EditText input) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    private void ConfirmDelete(Context context, final CourseModel course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.courses))
                .setMessage(context.getResources().getString(R.string.courses_delete_message))
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteCourse(course);
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false); // Sets whether this dialog is cancelable with the BACK key.
        builder.setNegativeButton(context.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false); // Prevent Cancel outside dialog
        alert.show();
    }

    boolean AddCourse(String nameCourse) {

        Log.d("Dao", "Course: " + nameCourse);
        if (nameCourse.trim().equals("")) {
            return false;
        } else {
            Course course = new Course(null, nameCourse);
            coursesDao.insert(course);
            Log.d("Dao", "Inserted new course, ID: " + course.getId());

            adapter.add(new CourseModel(course));
            Utils.ShowMessage(this, String.format("%s '%s'", getString(R.string.courses_add), nameCourse));
            return true;
        }
    }

    boolean EditCourse(String nameCourse, CourseModel courseModel) {

        Log.d("Dao", "Course: " + nameCourse);
        if (nameCourse.trim().equals("")) {
            return false;
        } else {
            Course course = coursesDao.queryBuilder().where(CourseDao.Properties.Id.eq(courseModel.getId())).unique();
            course.setName(nameCourse);
            coursesDao.update(course);
            Log.d("Dao", "Update course, ID: " + course.getId());

            courseModel.setName(course.getName());
            adapter.notifyDataSetChanged();
            Utils.ShowMessage(this, String.format("%s '%s'", getString(R.string.courses_edit), nameCourse));
            return true;
        }
    }

    void DeleteCourse(CourseModel courseModel){
        String nameCourse = courseModel.getName();
        Log.d("Dao", "Course: " + nameCourse);
        Course course = coursesDao.queryBuilder().where(CourseDao.Properties.Id.eq(courseModel.getId())).unique();

        Log.d("Dao", "Delete course, ID: " + course.getId());
        coursesDao.delete(course);

        adapter.remove(courseModel);
        adapter.notifyDataSetChanged();
        Utils.ShowMessage(this, String.format("%s '%s'", getString(R.string.courses_delete), nameCourse));
    }
}
