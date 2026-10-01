package com.nkp.accesorios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context c){ super(c,"nkp.db",null,4); }
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE productos(id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT NOT NULL,compra REAL DEFAULT 0,venta REAL DEFAULT 0,stock REAL DEFAULT 0,unidad TEXT DEFAULT 'unidad',factor REAL DEFAULT 1,sku TEXT DEFAULT '',minimo REAL DEFAULT 0)");
        db.execSQL("CREATE TABLE clientes(id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,telefono TEXT,documento TEXT,nota TEXT DEFAULT '')");
        db.execSQL("CREATE TABLE proveedores(id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,telefono TEXT,documento TEXT,nota TEXT DEFAULT '')");
        db.execSQL("CREATE TABLE ventas(id INTEGER PRIMARY KEY AUTOINCREMENT,cliente TEXT,telefono TEXT,documento TEXT,pago TEXT,total REAL,ganancia REAL,fecha INTEGER,estado TEXT DEFAULT 'ACTIVA')");
        db.execSQL("CREATE TABLE detalle_venta(id INTEGER PRIMARY KEY AUTOINCREMENT,venta_id INTEGER,producto_id INTEGER,nombre TEXT,cantidad REAL,unidad TEXT,precio REAL,total REAL,costo REAL,ganancia REAL)");
        db.execSQL("CREATE TABLE compras(id INTEGER PRIMARY KEY AUTOINCREMENT,proveedor TEXT,producto_id INTEGER,producto TEXT,cantidad REAL,costo REAL,total REAL,fecha INTEGER)");
        db.execSQL("CREATE TABLE cotizaciones(id INTEGER PRIMARY KEY AUTOINCREMENT,cliente TEXT,telefono TEXT,documento TEXT,total REAL,estado TEXT,fecha INTEGER,validez INTEGER)");
        db.execSQL("CREATE TABLE detalle_cotizacion(id INTEGER PRIMARY KEY AUTOINCREMENT,cotizacion_id INTEGER,producto_id INTEGER,nombre TEXT,cantidad REAL,unidad TEXT,precio REAL,total REAL)");
        db.execSQL("CREATE TABLE fiados(id INTEGER PRIMARY KEY AUTOINCREMENT,venta_id INTEGER,cliente TEXT,total REAL,pagado REAL,saldo REAL,fecha INTEGER,estado TEXT)");
        db.execSQL("CREATE TABLE pagos_fiado(id INTEGER PRIMARY KEY AUTOINCREMENT,fiado_id INTEGER,monto REAL,fecha INTEGER,nota TEXT)");
        db.execSQL("CREATE TABLE caja(id INTEGER PRIMARY KEY AUTOINCREMENT,tipo TEXT,concepto TEXT,monto REAL,fecha INTEGER)");
        db.execSQL("CREATE TABLE gastos(id INTEGER PRIMARY KEY AUTOINCREMENT,concepto TEXT,monto REAL,fecha INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV){
        if(oldV<2){ addColumn(db,"productos","factor","REAL DEFAULT 1"); addColumn(db,"productos","sku","TEXT DEFAULT ''"); addColumn(db,"productos","minimo","REAL DEFAULT 0"); }
        if(oldV<3){ addColumn(db,"clientes","nota","TEXT DEFAULT ''"); addColumn(db,"proveedores","nota","TEXT DEFAULT ''"); addColumn(db,"ventas","estado","TEXT DEFAULT 'ACTIVA'"); addColumn(db,"detalle_venta","costo","REAL DEFAULT 0"); }
        if(oldV<4){
            db.execSQL("CREATE TABLE IF NOT EXISTS proveedores(id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,telefono TEXT,documento TEXT,nota TEXT DEFAULT '')");
            db.execSQL("CREATE TABLE IF NOT EXISTS compras(id INTEGER PRIMARY KEY AUTOINCREMENT,proveedor TEXT,producto_id INTEGER,producto TEXT,cantidad REAL,costo REAL,total REAL,fecha INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS cotizaciones(id INTEGER PRIMARY KEY AUTOINCREMENT,cliente TEXT,telefono TEXT,documento TEXT,total REAL,estado TEXT,fecha INTEGER,validez INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS detalle_cotizacion(id INTEGER PRIMARY KEY AUTOINCREMENT,cotizacion_id INTEGER,producto_id INTEGER,nombre TEXT,cantidad REAL,unidad TEXT,precio REAL,total REAL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS fiados(id INTEGER PRIMARY KEY AUTOINCREMENT,venta_id INTEGER,cliente TEXT,total REAL,pagado REAL,saldo REAL,fecha INTEGER,estado TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS pagos_fiado(id INTEGER PRIMARY KEY AUTOINCREMENT,fiado_id INTEGER,monto REAL,fecha INTEGER,nota TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS caja(id INTEGER PRIMARY KEY AUTOINCREMENT,tipo TEXT,concepto TEXT,monto REAL,fecha INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS gastos(id INTEGER PRIMARY KEY AUTOINCREMENT,concepto TEXT,monto REAL,fecha INTEGER)");
        }
    }
    private void addColumn(SQLiteDatabase db,String table,String col,String def){ try{db.execSQL("ALTER TABLE "+table+" ADD COLUMN "+col+" "+def);}catch(Exception ignored){} }
}
