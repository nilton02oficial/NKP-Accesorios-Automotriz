
package com.nkp.accesorios;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {

    LinearLayout principal;
    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Venta> ventas = new ArrayList<>();
    android.content.SharedPreferences datos;

    static class Producto {
        String nombre;
        double compra, venta;
        int stock;

        Producto(String n, double c, double v, int s) {
            nombre = n;
            compra = c;
            venta = v;
            stock = s;
        }
    }

    static class Venta {
        String cliente, telefono, documento, pago, fecha;
        double total;

        Venta(String c, String t, String d, String p, String f, double total) {
            cliente = c;
            telefono = t;
            documento = d;
            pago = p;
            fecha = f;
            this.total = total;
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        datos = getSharedPreferences("NKP_DATOS", MODE_PRIVATE);
        cargarDatos();

        mostrarInicio();
    }

    TextView texto(String s, int tam) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(tam);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(20, 18, 20, 18);
        return t;
    }

    Button boton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(16);
        b.setAllCaps(false);
        return b;
    }

    void prepararPantalla(String titulo) {
        principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setPadding(20, 20, 20, 20);

        TextView cabecera = texto(titulo, 24);
        cabecera.setTextColor(Color.rgb(130, 0, 0));
        cabecera.setGravity(Gravity.CENTER);

        principal.addView(cabecera);

        setContentView(principal);
    }

    void mostrarInicio() {
        prepararPantalla("NKP ACCESORIOS AUTOMOTRIZ");

        TextView propietario = texto(
                "De: Nilton Mamani Calsina",
                15
        );
        propietario.setGravity(Gravity.CENTER);
        principal.addView(propietario);

        principal.addView(texto(
                "Sistema de ventas e inventario",
                18
        ));

        Button venta = boton("🛒  NUEVA VENTA");
        Button productosBtn = boton("📦  PRODUCTOS E INVENTARIO");
        Button ventasBtn = boton("📋  HISTORIAL DE VENTAS");
        Button cajaBtn = boton("💰  CAJA");
        Button acerca = boton("ℹ️  INFORMACIÓN");

        principal.addView(venta);
        principal.addView(productosBtn);
        principal.addView(ventasBtn);
        principal.addView(cajaBtn);
        principal.addView(acerca);

        venta.setOnClickListener(v -> nuevaVenta());
        productosBtn.setOnClickListener(v -> mostrarProductos());
        ventasBtn.setOnClickListener(v -> mostrarVentas());
        cajaBtn.setOnClickListener(v -> mostrarCaja());

        acerca.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("NKP Accesorios Automotriz")
                        .setMessage(
                                "Sistema de ventas e inventario\n\n" +
                                "De: Nilton Mamani Calsina"
                        )
                        .setPositiveButton("OK", null)
                        .show()
        );
    }

    void mostrarProductos() {
        prepararPantalla("📦 PRODUCTOS");

        Button agregar = boton("➕ Agregar producto");
        Button volver = boton("⬅ Volver");

        principal.addView(agregar);

        if (productos.size() == 0) {
            principal.addView(texto(
                    "No hay productos registrados.",
                    17
            ));
        }

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);

            Text
