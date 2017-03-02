package teezha.bcit.addingtomapserver;

import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.core.map.FeatureType;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.SymbolHelper;

import java.util.ArrayList;

/**
 * Created by a00987765 on 3/1/2017.
 */

public class makeFeatureTypeList {
    /** ==============================================================================
     * Method : makeFeatureTypeList
     *
     * Current Author: You!
     *
     * Previous Author: Robert Hewlett
     *
     * Contact Info: somebody@somewhere.com
     *
     * Purpose : Populate a list using the data from the service: type name and
     * id
     * then the ArcGIS symbol and the symbol converted to an android bitmap
     *
     * Dependencies: Quick Toast
     *
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     *
     * =============================================================================
     */
    public ArrayList<FeatureTypeInfo> makeFeatureTypeList(ArcGISFeatureLayer agsFeatureLayer) {
        FeatureType[] ftypes = agsFeatureLayer.getTypes();
        ArrayList<FeatureTypeInfo> aList = new ArrayList<FeatureTypeInfo>();

        FeatureTypeInfo fInfo;
        Symbol tmpSymbol;
        Graphic tmpGraphic;
        /** ===========================================================
         * For each template/prototype add it to the list
         * convert its symbol to an Android bitmap
         * ============================================================
         */
        for (int i = 0; i < ftypes.length; i++) {
            fInfo = new FeatureTypeInfo();
            fInfo.setId(Integer.parseInt(ftypes[i].getId()));
            fInfo.setName(ftypes[i].getName());
            tmpGraphic = agsFeatureLayer.createFeatureWithTemplate(
                    ftypes[i].getTemplates()[0], null);
            tmpSymbol = agsFeatureLayer.getRenderer().getSymbol(tmpGraphic);
            fInfo.setSymbol(tmpSymbol);
            fInfo.setBitmap(SymbolHelper.getLegendImage(tmpSymbol, 80, 80));
            aList.add(fInfo);
        }
        return aList;
    }
}
