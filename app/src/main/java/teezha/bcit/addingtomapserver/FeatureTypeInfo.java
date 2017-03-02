package teezha.bcit.addingtomapserver;

import android.graphics.Bitmap;

import com.esri.core.symbol.Symbol;

/**
 * Created by a00987765 on 3/1/2017.
 */

public class FeatureTypeInfo {
        private int id;
        private Bitmap bitmap;
        private String name;
        private Symbol symbol;


        // =============================
        // Getters and setters
        // =============================


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Symbol getSymbol() {
            return symbol;
        }

        public void setSymbol(Symbol symbol) {
            this.symbol = symbol;
        }
    }



