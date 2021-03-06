package teezha.bcit.addingtomapserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureEditResult;
import com.esri.core.map.FeatureTemplate;
import com.esri.core.map.FeatureType;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.SymbolHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /** ==================================================
     * Added in module 07: Attribute Dialog
     * ===================================================
     */
    final int ADD_FEAT_CODE = 1;
    final int DELETE_FEAT_CODE = 2;
    final int UPDATE_FEAT_CODE = 3;
    final int REQUEST_OK = 1;

    /** ================================================================
     *
     *  This section organizes class fields
     *
     * ================================================================== */


    /**
     * ==================================================
     * Common strings variables required by the activity
     * ==================================================
     */
    String baseMapServiceURL;
    String poiFeatureLayerURL;
    String poiFeatureServiceURL;
    String poiMapServiceURL;

    /**
     * ==================================================
     * Common ArcGIS variables for BCIT layers
     * <p>
     * If you started with an android application project
     * right click on the project --> ArcGIS tools
     * --> convert to ArcGIS project
     * <p>
     * import com.esri.android.map.MapView;
     * import com.esri.android.map.ags.ArcGISFeatureLayer;
     * import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
     * import com.esri.core.map.Feature;
     * ==================================================
     */
    MapView mapView;
    ArcGISTiledMapServiceLayer poiTiledLayer;
    ArcGISFeatureLayer poiFeatureLayer;
    Feature currentFeature;

    /**
     * ==================================================
     * Extra variables for BCIT layers
     * ==================================================
     */
    GraphicsLayer graphicsLayer;
    FeatureType[] featureTypes;
    SimpleMarkerSymbol pointSymbol;
    CallbackListener<FeatureEditResult[][]> editCallBack;



    /** ==================================================
     * Added in module 07: Spinner template picker
     * ==================================================
     */
    ArrayList<FeatureTypeInfo> aList;
    Spinner spnFeatureTypeList;
    int activeFeatureType = -1;

    /** =====================================
     * Private fields for FeatureTypeInfo
     * =====================================
     */
    private int id;
    private Bitmap bitmap;
    private String name;
    private Symbol symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = getMapView();
        mapView.addLayer(getPoiTiledLayer());
        mapView.addLayer(getPoiFeaturelayer());

        postMapInit(mapView);

        addLongPress(mapView);
        addSinglePress(mapView);

        setEditCallBack();

        /** ==================================================
         * Added in module 07: Hook the spinner to the UI
         * Hook the spinner to its listener
         * ==================================================
         * */
        spnFeatureTypeList = (Spinner) findViewById(R.id.featureTypeList);
        addItemSelectedListener(spnFeatureTypeList);


    }


    /** ================================================================
     *
     *  This section organizes the getters and setters
     *
     * ================================================================== */

    /**
     * ==============================================================================
     * Method : addSinglePress
     * <p>
     * Current Author: You!
     * <p>
     * Previous Author: Robert Hewlett
     * <p>
     * Contact Info: somebody@somewhere.com
     * <p>
     * Purpose : Add a single tap event to the map. Will eventually save a point
     * to the graphics layers
     * <p>
     * Dependencies: Quick Toast
     * <p>
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     * <p>
     * =============================================================================
     */



    /**
     * ==================================================
     * Read only getter for the activity
     * ==================================================
     */
    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK) {

            /**
             * ===========================================================
             * If there is a red diamond on the screen
             * ===========================================================
             */

            FeatureTemplate featureTemplate = getPoiFeaturelayer().getTypes()[0].getTemplates()[0];

            Graphic gIn, gOut;

            if (getGraphicsLayer().getGraphicIDs() != null) {
                // ======================================================
                // update the default attributes on the template to the new
// values  template getPrototype.put("<column_name>","<value>")
// the column names in the service are:
//  user_name
//  comment
// ======================================================



                featureTemplate.getPrototype().put("user_name", data.getStringExtra("user_name"));
                featureTemplate.getPrototype().put("comment", data.getStringExtra("user_comment"));


                /**
                 * ===========================================================
                 * get the geometry from the graphics layer via the graphic
                 * ===========================================================
                 */

                gIn=getGraphicsLayer().getGraphic(getGraphicsLayer().getGraphicIDs()[0]);

                /**
                 * ===========================================================
                 * make a new graphic with the updated template and geom
                 * ===========================================================
                 */

                gOut = getPoiFeaturelayer().createFeatureWithTemplate(featureTemplate,gIn.getGeometry());
                /**
                 * ===========================================================
                 * organize the new graphic (geom + att) in a geometry array
                 * ===========================================================
                 */

                Graphic[] graphicArray = {gOut};
                /**
                 * ===========================================================
                 * apply the edits; in this case just one insert
                 * ===========================================================
                 */
                getPoiFeaturelayer().applyEdits(graphicArray,null,null,getEditCallBack());


            }
            Log.d("8010", "The requestcode was " + requestCode);
            Log.d("8010", "The resultcode was " + resultCode);

            if (requestCode == ADD_FEAT_CODE) {
                if (resultCode == RESULT_OK) {
                    quickToast("returned ok from dialog");


                }
                else
                {
                    quickToast("User canceled clearing graphics layer");
                }
                getGraphicsLayer().removeAll();
            }
        }
        }

    /**
     * =============================================================================
     * setter for poi map service: capabilities identify
     * =============================================================================
     */
    private void setPoiMapServiceURL() {
        /**
         * =============================================================================
         * need a string for baseMapServiceURL (strings.xml)
         * =============================================================================
         */
        poiMapServiceURL = getApplication().getResources().getString(
                R.string.poiMapServiceURL);
    }

    /**
     * =============================================================================
     * getter for poi map service: capabilities identify
     * =============================================================================
     */
    private String getPoiMapServiceURL() {
        if (poiMapServiceURL == null) {
            setPoiMapServiceURL();
        }
        return poiMapServiceURL;
    }


    /**
     * =============================================================================
     * setter for the Base map URL
     * =============================================================================
     */
    private void setPoiFeatureLayerURL() {
        /**
         * =============================================================================
         * need a string for baseMapServiceURL (strings.xml)
         * =============================================================================
         */
        poiFeatureLayerURL = getApplication().getResources().getString(
                R.string.poiFeatureLayerURL);
    }

    /**
     * =============================================================================
     * getter for the base map URL
     * =============================================================================
     */
    private String getPoiFeaturelayerURL() {
        if (poiFeatureLayerURL == null) {
            setPoiFeatureLayerURL();
        }
        return poiFeatureLayerURL;
    }

    /**
     * =============================================================================
     * setter for the Base map URL
     * =============================================================================
     */
    private void setPoiFeatureServiceURL() {
               /*
        * =============================================================================
		* need a string for baseMapServiceURL (strings.xml)
		* =============================================================================
                */
        poiFeatureServiceURL = getApplication().getResources().getString(
                R.string.poiFeatureServiceURL);
    }

    /**
     * =============================================================================
     * getter for the base map URL
     * =============================================================================
     */
    private String getPoiFeatureServiceURL() {
        if (poiFeatureServiceURL == null) {
            setPoiFeatureLayerURL();
        }
        return poiFeatureServiceURL;
    }

    /**
     * =============================================================================
     * setter for the mapURL
     * =============================================================================
     */
    private void setBaseMapServiceURL() {
        /**
         * =============================================================================
         * need a string for baseMapServiceURL (strings.xml)
         *
         * =============================================================================
         */
        baseMapServiceURL = getApplication().getResources().getString(
                R.string.baseMapServiceURL);
    }

    /**
     * =============================================================================
     * getter for the mapURL
     * =============================================================================
     */
    private String getBaseMapServiceURL() {
        if (baseMapServiceURL == null) {
            setBaseMapServiceURL();
        }
        return baseMapServiceURL;
    }

    /**
     * =============================================================================
     * getter for the main map
     * =============================================================================
     */
    private MapView getMapView() {
        if (mapView == null) {
            setMapView();
        }
        /**
         * =============================================================================
         * if MapView is underlined in red, change the name to match your MapView
         * =============================================================================
         */
        return mapView;
    }

    /**
     * =============================================================================
     * setter for the main map
     * =============================================================================
     */
    private void setMapView() {
        /**
         * ========================================================================
         * if mapView is underlined in red then sync class-level var names
         * if R.id.map is underlined in red ensure that you added the xml fragment
         * such as "MapView Generic" to a layout
         * ========================================================================
         */
        mapView = (MapView) findViewById(R.id.map);
    }


    /**
     * =============================================================================
     * getter for the map's base layer
     * =============================================================================
     */
    private ArcGISTiledMapServiceLayer getPoiTiledLayer() {
        if (poiTiledLayer == null) {
            setPoiTiledLayer();
        }
        return poiTiledLayer;
    }


    /**
     * =============================================================================
     * setter for the poi feature Layer
     * =============================================================================
     */
    private void setPoiFeaturelayer() {
        poiFeatureLayer = new ArcGISFeatureLayer(getPoiFeaturelayerURL(),
                ArcGISFeatureLayer.MODE.SNAPSHOT);
    }

    /**
     * =============================================================================
     * getter for the poi feature Layer
     * =============================================================================
     */
    private ArcGISFeatureLayer getPoiFeaturelayer() {
        if (poiFeatureLayer == null) {
            setPoiFeaturelayer();
        }
        return poiFeatureLayer;
    }

    /**
     * =============================================================================
     * setter for the map's base layer
     * =============================================================================
     */

    private void setPoiTiledLayer() {

        /**
         * =============================================================================
         * if R.id.map make sure you add the xml fragment MapView Generic to a layout
         * file
         * =============================================================================
         */
        poiTiledLayer = new ArcGISTiledMapServiceLayer(getBaseMapServiceURL());
    }

    private void addSinglePress(MapView map) {
        map.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {

                // ======================================================
// send a message to the log
// ======================================================

                Intent intent= new Intent(getActivity(), GetAttributesDialog.class);
                startActivityForResult(intent, REQUEST_OK);

            }
        }); // end of the inner class

    } // Add a single tap listener
    /**
     * =============================================================================
     * setter for the default point symbol
     * <p>
     * import android.graphics.Color;
     * <p>
     * =============================================================================
     */
    private void setPointSymbol() {
        pointSymbol = new SimpleMarkerSymbol(Color.BLUE, 20,
                SimpleMarkerSymbol.STYLE.SQUARE);
    }

    /**
     * =============================================================================
     * getter for the map's feature layer
     * =============================================================================
     */
    private SimpleMarkerSymbol getPointSymbol() {
        if (pointSymbol == null) {
            setPointSymbol();
        }
        return pointSymbol;
    }


    /**
     * ==============================================================================
     * Method : setEditCallBack
     * <p>
     * Current Author: You!
     * <p>
     * Previous Author: Robert Hewlett
     * <p>
     * Contact Info: somebody@somewhere.com
     * <p>
     * Purpose : setup a callback listener for when you save edits
     * <p>
     * <p>
     * Dependencies: Quick Toast and slow toast
     * <p>
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     * <p>
     * =============================================================================
     */
    private void setEditCallBack() {
        editCallBack = new CallbackListener<FeatureEditResult[][]>() {

            @Override
            public void onCallback(FeatureEditResult[][] results) {
                if (results[0][0].isSuccess()) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            quickToast("The edits were sucessfull!");
                            getGraphicsLayer().removeAll();

                        }
                    });

                } else {
                    final String message = results[0][0].getError().getDescription();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            slowToast(message);
                        }
                    });
                }

            }

            @Override
            public void onError(Throwable e) {
                slowToast(e.getMessage());
            }

        }; // end of the inner class
    } // end of the method


    /**
     * ==============================================================================
     * Method : getEditCallBack
     * <p>
     * Current Author: You!
     * <p>
     * Previous Author: Robert Hewlett
     * <p>
     * Contact Info: somebody@somewhere.com
     * <p>
     * Purpose : return the edit call back
     * <p>
     * <p>
     * Dependencies: Quick Toast and slow toast
     * <p>
     * Modification Log :
     * --> Created FEB-18-2016 (rh)
     * --> Updated MMM-DD-YYYY (fl)
     * <p>
     * =============================================================================
     */
    private CallbackListener<FeatureEditResult[][]> getEditCallBack() {

        if (editCallBack == null) {
            setEditCallBack();
        }

        return editCallBack;
    }

    /** ================================================================
     *
     *  This section organizes the general/convenience methods
     *
     * ================================================================== */




    /**
     * ==============================================================================
     * Method : postMapInit
     * <p>
     * Current Author: You!
     * <p>
     * Previous Author: Robert Hewlett
     * <p>
     * Contact Info: somebody@somewhere.com
     * <p>
     * Purpose : Certain things cannot be done until the map or a layer is
     * awake otherwise you will get null pointer exceptions
     * <p>
     * Dependencies: Quick Toast
     * import com.esri.android.map.event.OnStatusChangedListener;
     * import com.esri.android.map.event.OnStatusChangedListener.STATUS;
     * <p>
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     * <p>
     * =============================================================================
     */
    private void postMapInit(MapView map) {
        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (OnStatusChangedListener.STATUS.INITIALIZED == status) {
                    /**
                     * ===========================================================
                     * Your code goes here e.g. get the graphics layer
                     * ===========================================================
                     */

                } // end of if map ready
                if (status == STATUS.LAYER_LOADED) {
                    /**
                     * ===========================================================
                     * Your code goes here. For example
                     *      get the featureTypes from the layer
                     *      setup the call back listener
                     * ===========================================================
                     */
                                    if (source instanceof ArcGISTiledMapServiceLayer) {
                        getMapView().addLayer(getGraphicsLayer());
                        zoomToConnector();
                    }

                    if (source instanceof ArcGISFeatureLayer) {
                        aList = makeFeatureTypeList(getPoiFeaturelayer());
                        FeatureTypeAdapter featureTypeAdapter = new FeatureTypeAdapter(getActivity(),R.layout.feature_type_info_view, aList);
                        spnFeatureTypeList.setAdapter(featureTypeAdapter);

                    }

                } // end if layer loaded
            } // end of on status changed
        });
    } // end of the method: PostMapInt

    public void zoomToConnector() {
        /**
         * =============================================================================
         * zoom the map to a known location: crescent beach
         * =============================================================================
         */
        double lat, lon;
        lat = 49.25034819689476;
        lon = -123.0027188915095;
        boolean animated = true;
        double scale = 1000;
        /**
         * =============================================================================
         * 2 step zoom: center then scale
         * =============================================================================
         */
        getMapView().centerAt(lat, lon, animated);
        getMapView().setScale(scale);

    }





    /**
     * =============================================================================
     * setter for the map's graphics layer
     * =============================================================================
     */
    private void setgraphicsLayer() {
        graphicsLayer = new GraphicsLayer();
    }

    /**
     * =============================================================================
     * getter for the map's graphics layer
     * =============================================================================
     */
    private GraphicsLayer getGraphicsLayer() {
        if (graphicsLayer == null) {
            setgraphicsLayer();
        }
        return graphicsLayer;
    }


    /**
     * ==============================================================================
     * Method : addLongPress
     * <p>
     * Current Author: You!
     * <p>
     * Previous Author: Robert Hewlett
     * <p>
     * Contact Info: somebody@somewhere.com
     * <p>
     * Purpose : Add a long press event to the map. Will eventually add a point
     * to
     * To the graphics layers
     * <p>
     * Dependencies: Quick Toast
     * <p>
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     * <p>
     * =============================================================================
     */
    private void addLongPress(MapView map) {
        map.setOnLongPressListener(new OnLongPressListener() {

            @Override
            public boolean onLongPress(float x, float y) {
                /**
                 *===========================================
                 * Your code goes here
                 *===========================================
                 */
                // ======================================================
// send a message to the log
// ======================================================
                Log.i("GIST-8010", "Inside long press");
                getGraphicsLayer().removeAll();
                Point pt = getMapView().toMapPoint(x, y);

                Graphic pointGraphic = new Graphic(pt, getPointSymbol());
                graphicsLayer.addGraphic(pointGraphic);


                return false;
            }
        }); // end of the inner classs
    } // end of the long press AKA tap and hold

    /**
     * =============================================================================
     * This method simplifies making a toast --> quick
     * =============================================================================
     */
    public void quickToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /**
     * =============================================================================
     * This method simplifies making a toast --> slow
     * =============================================================================
     */
    public void slowToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

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

    /** ==============================================================================
     * Method : addItemSelectedListener
     *
     * Current Author: You!
     *
     * Previous Author: Robert Hewlett
     *
     * Contact Info: somebody@somewhere.com
     *
     * Purpose : Update the class field activeFeatureType to the current position
     *
     * Dependencies: Quick Toast
     *
     * Modification Log :
     * --> Created MMM-DD-YYYY (fl)
     * --> Updated MMM-DD-YYYY (fl)
     *
     * =============================================================================
     */
    public void addItemSelectedListener(Spinner spin) {
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                activeFeatureType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    } // end of the method


}
