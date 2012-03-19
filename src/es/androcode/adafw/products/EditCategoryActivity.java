/*
 * A project sample for AdaFramework (http://www.adaframework.com/)
 * 
 * Copyright (C) 2012 Androcode
 * <http://www.androcode.es>
 * 
 * This project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.androcode.adafw.products;

import com.desandroid.framework.ada.Entity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;

public class EditCategoryActivity extends Activity {

    public static final String ID_CATEGORY = "idCategory";
    /**
     * Activity DataContext.
     */
    private ApplicationDataContext appDataContext;
    private EditText name;
    private EditText description;
    private Category category;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_category);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        
        long idCategory = getIntent().getLongExtra(ID_CATEGORY, 0);
        category = queryCategory(idCategory);
        if (category == null) {
            finish();
        } else {
            name.setText(category.getName());
            description.setText(category.getDescription());
            setResult(RESULT_CANCELED);
        }
    }
    
    public void save(View view) {
        String newName = name.getText().toString();
        if (newName.trim().length() > 0) {
            // Guardamos los nuevos datos
            category.setName(newName);
            category.setDescription(description.getText().toString());
            // Salvamos
            ApplicationDataContext dataContext = getApplicationDataContext();
            if (dataContext != null) {
                try {                
                    dataContext.categoryDao.add(category);
                    dataContext.categoryDao.save();
                    setResult(RESULT_OK);
                } catch (Exception e) {
                    Log.e("Androcode", "Error guardando categoria: " + e.getMessage());
                }
            }
            finish();
        } else {
            Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        finish();
    }
    
    private ApplicationDataContext getApplicationDataContext() {
        if (appDataContext == null) {
            try {
                appDataContext = new ApplicationDataContext(this);
            } catch (Exception e) {
                Log.e("Androcode", "Error inicializando ApplicationDataContext: " + e.getMessage());
            }
        }
        return appDataContext;
    }
    
    private Category queryCategory(long idCategory) {
        Category category = null;
        ApplicationDataContext dataContext = getApplicationDataContext();
        if (dataContext != null) {            
            if (idCategory == 0) {
                // Es una categoría nueva, creamos el objeto
                category = new Category("", "");
                category.setStatus(Entity.STATUS_NEW);
            } else {
                // Recuperamos la categoría
                try {
                    category = dataContext.categoryDao.getElementByID(idCategory);
                    category.setStatus(Entity.STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e("Androcode", "Error recuperando categoria: " + e.getMessage());
                }
                ((TextView) findViewById(R.id.edit_title)).setText(R.string.edit_category);
            }
        }
        return category;
    }

}
