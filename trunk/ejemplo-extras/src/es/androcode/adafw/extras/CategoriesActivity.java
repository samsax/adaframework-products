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

package es.androcode.adafw.extras;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.desandroid.framework.ada.Entity;

import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;

/**
 * Actividad que muestra la lista de Categorías
 * @author fedeproex
 *
 */
public class CategoriesActivity extends ListActivity {

    private static final int REQUEST_EDIT = 1;
    private static final int MENU_DELETE = 1;
    private static final int MENU_EDIT = 2;
    
    /**
     * Activity DataContext.
     */
    private ApplicationDataContext appDataContext;
    private MyAdapter adapter;
    protected LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nos registramos para recibir eventos de pulsación larga
        registerForContextMenu(getListView());
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        try {
            // Creamos un objeto ApplicationDataContext
            appDataContext = new ApplicationDataContext(this);
            
            // Creamos el adapter que utilizará el DAO
            adapter = new MyAdapter(this, R.layout.category);
            setListAdapter(adapter);
            
            // Le decimos al data context que rellene el adapter
            appDataContext.categoryDao.fill("name");
            appDataContext.categoryDao.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("Androcode", "Error creando vista", e);
        }
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Al pulsar nos vamos a la edición directamente
        Intent intent = new Intent(this, EditCategoryActivity.class);
        intent.putExtra(EditCategoryActivity.ID_CATEGORY, id);
        startActivityForResult(intent, REQUEST_EDIT);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // Añadimos la opción de eliminar o editar
        menu.add(0, MENU_DELETE, 0, R.string.delete);
        menu.add(0, MENU_EDIT, 0, R.string.edit);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("Androcode", "Error recuperando MenuInfo", e);
            return false;
        }
        
        Category category = appDataContext.categoryDao.get(info.position);
        if (item.getItemId() == MENU_DELETE) {
            category.setStatus(Entity.STATUS_DELETED);
            try {
                appDataContext.categoryDao.save(category);
                reloadList();
            } catch (Exception e) {
                Log.e("Androcode", "Error eliminando categoría", e);
            }            
        } else {
            long id = category.getID();
            Intent intent = new Intent(this, EditCategoryActivity.class);
            intent.putExtra(EditCategoryActivity.ID_CATEGORY, id);
            startActivityForResult(intent, REQUEST_EDIT);
        }
        
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            reloadList();
        }
    }
    
    /**
     * Método de ayuda que vuelve a consultar la base de datos
     * y avisa al adapter de que los datos han cambiado
     */
    private void reloadList() {
        adapter.notifyDataSetChanged();
    }
    
    /**
     * Adapter que utiliza el DAO para rellenar la lista
     * 
     */
    class MyAdapter extends ArrayAdapter<Category> {
    	
    	private int resource;

        public MyAdapter(Context context, int resource) {
			super(context, resource);
			this.resource = resource;
		}

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = inflater.inflate(resource, null);
            }
            Category category = (Category) getItem(position);
            ((TextView) itemView.findViewById(R.id.name)).setText(category.getName());
            ((TextView) itemView.findViewById(R.id.description)).setText(category.getDescription());
            return itemView;
        }
        
    }
}