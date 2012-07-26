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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;
import es.androcode.adafw.products.model.Product;

public class MainActivity extends Activity {
	
	public static final String BASE_DE_DATOS = "BASE_DE_DATOS";
	
	private boolean listo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        listo = sp.getBoolean(BASE_DE_DATOS, false);
        
        if (!listo) {
        	new RellenaBDTask().execute(new Void[0]);
        }
    }
    
    public void viewCategories(View view) {
    	iniciaActivitidad(CategoriesActivity.class);
    }
    
    public void newCategory(View view) {
    	iniciaActivitidad(EditCategoryActivity.class);
    }
    
    public void viewProducts(View view) {
    	iniciaActivitidad(ProductsActivity.class);
    }
    
    public void newProduct(View view) {
    	iniciaActivitidad(EditProductActivity.class);
    }
    
    private void iniciaActivitidad(Class<?> actividad) {
    	if (listo) {
    		startActivity(new Intent(this, actividad));
    	} else {
    		Toast.makeText(this, "Se está preparando la base de datos de ejemplo", Toast.LENGTH_SHORT).show();
    	}
    }
    
    class RellenaBDTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {		
			try {
	            // Creamos un objeto ApplicationDataContext
				ApplicationDataContext appDataContext = new ApplicationDataContext(MainActivity.this);
	            // 
				Category c = new Category("Alimentación", "Productos de alimentación");
				appDataContext.categoryDao.add(c);
				Product p = new Product("Leche", c, 12, 0.65, 5000);
				appDataContext.productDao.add(p);
				p = new Product("Galletas", c, 3, 1.5, 2500);
				appDataContext.productDao.add(p);
				p = new Product("Arroz", c, 1, 1.25, 35000);
				appDataContext.productDao.add(p);
				
				c = new Category("Limpieza", "Productos de limpieza");
				appDataContext.categoryDao.add(c);
				p = new Product("Lejía", c, 1, 1.5, 6000);
				appDataContext.productDao.add(p);
				p = new Product("Lavavajillas", c, 1, 2.5, 3500);
				appDataContext.productDao.add(p);
				p = new Product("Balleta", c, 2, 1.25, 25000);
				appDataContext.productDao.add(p);
				p = new Product("Fregona", c, 1, 3.55, 1000);
				appDataContext.productDao.add(p);
				
				c = new Category("Electrónica", "Cine, Música, Videojuegos...");
				appDataContext.categoryDao.add(c);
				p = new Product("Televisión", c, 1, 699, 100);
				appDataContext.productDao.add(p);
				p = new Product("Mini Cadena", c, 1, 350, 50);
				appDataContext.productDao.add(p);
				
				appDataContext.categoryDao.save();
				appDataContext.productDao.save();
				
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
				editor.putBoolean(BASE_DE_DATOS, true);
				editor.commit();
	            
	        } catch (Exception e) {
	            Log.e("Androcode", "Error creando vista", e);
	        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listo = true;
		}
    	
    }
    
}