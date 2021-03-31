package xmltools;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReglasXML {
    public ReglasXML(){    }

    private static boolean tieneHijosTipoElementNode(Node node){
        NodeList nodeChilds = node.getChildNodes();
        for (int i = 0; i < nodeChilds.getLength();i++) {
            Node nodeChild = nodeChilds.item(i);
            if (nodeChild.getNodeType() == Node.ELEMENT_NODE ) return true;
        }
        return false;
    }

    public static boolean eliminaNodo(Node nodo) {
        boolean ret = false;
        String ancestors = getPathDelNodo(nodo);
        if (ancestors.equals("WMS_Capabilities/Capability/Request/esri_wms:GetStyles")) {
            ret = true;
        }
        if (ancestors.equals("WMS_Capabilities/Capability/Layer/Layer/Style/StyleSheetURL")) {
            ret = true;
        }
        return ret;
    }
    public static void addAtributo(Node nodo){
        String ancestors = getPathDelNodo(nodo);
        if (ancestors.equals("WMS_Capabilities/Service/KeywordList/Keyword")) {
            String text = getTextoDelNodo(nodo);
            for(String s:textoConAtributoList){
                if (text.compareTo(s)==0){
                    //tag += " vocabulary=\"GEMET - INSPIRE themes, version 1.0, 2008-06-01\"";
                    ((Element)nodo).setAttribute("vocabulary","GEMET - INSPIRE themes, version 1.0, 2008-06-01");

                    break;
                }
            }
        }
    }

    private static String getTextoDelNodo(Node node) {
        String txt = "";
        NodeList list = node.getChildNodes();
        StringBuilder textContent = new StringBuilder();
        for (int i = 0; i < list.getLength(); ++i) {
            Node child = list.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                txt = child.getTextContent();
                txt = txt.replace("\n", " ");
                if (txt.trim().length() > 0)  textContent.append(txt);
            }
        }
        txt = textContent.toString().trim();
        return txt;
    }
    private static List<String> textoConAtributoList = Arrays.asList(
            new String("Addresses"),
            new String("Direcciones"),
            new String("Administrative units"),
            new String("Unidades administrativas"),
            new String("Agricultural and aquaculture facilities"),
            new String("Instalaciones agrícolas y de acuicultura"),
            new String("Area management/restriction/regulation zones and reporting units"),
            new String("Zonas sujetas a ordenación, a restricciones o reglamentaciones y unidades de notificación"),
            new String("Atmospheric conditions"),
            new String("Condiciones atmosféricas"),
            new String("Bio-geographical regions"),
            new String("Regiones biogeográficas"),
            new String("Buildings"),
            new String("Edificios"),
            new String("Cadastral parcels"),
            new String("Parcelas catastrales"),
            new String("Coordinate reference systems"),
            new String("Sistemas de coordenadas de referencia"),
            new String("Elevation"),
            new String("Elevaciones"),
            new String("Energy resources"),
            new String("Recursos energéticos"),
            new String("Environmental monitoring facilities"),
            new String("Instalaciones de observación del medio ambiente"),
            new String("Geographical grid systems"),
            new String("Sistema de cuadrículas geográficas"),
            new String("Geographical names"),
            new String("Nombres geográficos"),
            new String("Geology"),
            new String("Geología"),
            new String("Habitats and biotopes"),
            new String("Hábitats y biotopos"),
            new String("Human health and safety"),
            new String("Salud y seguridad humanas"),
            new String("Hydrography"),
            new String("Hidrografía"),
            new String("Land cover"),
            new String("Cubierta terrestre"),
            new String("Land use"),
            new String("Uso del suelo"),
            new String("Meteorological geographical features"),
            new String("Aspectos geográficos de carácter meteorológico"),
            new String("Mineral resources"),
            new String("Recursos minerales"),
            new String("Natural risk zones"),
            new String("Zonas de riesgos naturales"),
            new String("Oceanographic geographical features"),
            new String("Rasgos geográficos oceanográficos"),
            new String("Orthoimagery"),
            new String("Ortoimágenes"),
            new String("Population distribution — demography"),
            new String("Distribución de la población — demografía"),
            new String("Production and industrial facilities"),
            new String("Instalaciones de producción e industriales"),
            new String("Protected sites"),
            new String("Lugares protegidos"),
            new String("Sea regions"),
            new String("Regiones marinas"),
            new String("Soil"),
            new String("Suelo"),
            new String("Species distribution"),
            new String("Distribución de las especies"),
            new String("Statistical units"),
            new String("Unidades estadísticas"),
            new String("Transport networks"),
            new String("Redes de transporte"),
            new String("Utility and governmental services"),
            new String("Servicios de utilidad pública y estatales"));





    // Path de miNodo = gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString
    // Path relativo del nodo pariente que busco:
    // gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString
    // El ancestro común es "gmd:descriptiveKeywords"
    private static Node getNodoPariente(Node miNodo,String pathNodoPariente){
        //Hay que recorrer los ancestros
        Node temp = miNodo;
        String tag = temp.getNodeName();
        String paths[] = pathNodoPariente.split("/");
        //path[0] debe contener el ancestro común
        while (!paths[0].equals(tag) && !tag.equals("#document")){
            temp = temp.getParentNode();
            tag = temp.getNodeName();
        }
        // Nodo temp es el ancestro común
       //
       //  Buscamos hacia abajo recorriendo el path del pariente desde el ancestro común
        for (int j = 1; j< paths.length; j++ ) {
            NodeList listaDeHijos = temp.getChildNodes();
            for (int i = 0; i < temp.getChildNodes().getLength(); i++) {
                Node nodo = listaDeHijos.item(i);
                if (nodo.getNodeType() != Node.ELEMENT_NODE) continue;
                if (nodo.getNodeName().equals(paths[j])){
                    temp = nodo;
                    break;
                }
            }
        }
        return temp;
    }
    private static String getPathDelNodo(Node nodo){
        String nodeName = nodo.getNodeName();
        String path = "";
        int cont = 0;
        do {
            path = nodeName + "/" + path;
            nodo = nodo.getParentNode();
            nodeName = nodo.getNodeName();
        }while (!nodeName.equals("#document") && ++cont < 100);
        if ( path.length() > 0) path = path.substring(0, path.length() - 1);
        return path;
    }

    // Métodos públicos de la clase para hacer cambios en el  "xml"
    public static String changeTAG(Node nodo){
        String tag = nodo.getNodeName();
        String ancestors = getPathDelNodo(nodo);
        if (ancestors.equals("gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString")) {
            Node parienteComun = getNodoPariente(nodo, "gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString");
        }

        for (int i= 0; i < NodePathList.size(); i++){
            if (NodePathList.get(i).equals(ancestors)){
                return newTagList.get(i);
            }
        }

        return tag;
    }
    

    private static List<String> NodePathList = Arrays.asList(
            "gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString",
            "gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywordsgmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString",
            "C");
    private static List<String> newTagList = Arrays.asList(
            "gmx:Anchor",
            "gmx:Anchor",
            "F");

    //////////////////////////////////////////////////////////

    
    
    
    
    public static Atributo newAtributo(Node nodo){
        Atributo atr = null;
        String ancestors = getPathDelNodo(nodo);

        return atr;
    }

    private class Atributo{
        private int id;
        private String nombreAtr;
        private String valorAtr;
        public Atributo(int id,String n,String v){
            this.id = id;
            nombreAtr = n;
            valorAtr = v;
        }
        public int getId() {
            return id;
        }
        public String getNombreAtr() {
            return nombreAtr;
        }
        public String getValorAtr() {
            return valorAtr;
        }
    }
    private List<Atributo> AtributosList = Arrays.asList(
            new Atributo(1,"vocabulary","GEMET - INSPIRE themes, version 1.0, 2008-06-01"),
            new Atributo(101,"xlink:href","http://inspire.ec.europa.eu/metadata-codelist/SpatialScope/national"),
            new Atributo(2,"xlink:href","http://inspire.ec.europa.eu/metadata-codelist/SpatialScope/regional"),
            new Atributo(3,"xlink:href","http://inspire.ec.europa.eu/theme/ad"),
            new Atributo(4,"xlink:href","http://inspire.ec.europa.eu/theme/au"),
            new Atributo(5,"xlink:href","http://inspire.ec.europa.eu/theme/af"),
            new Atributo(6,"xlink:href","http://inspire.ec.europa.eu/theme/am"),
            new Atributo(7,"xlink:href","http://inspire.ec.europa.eu/theme/ac"),
            new Atributo(8,"xlink:href","http://inspire.ec.europa.eu/theme/br"),
            new Atributo(9,"xlink:href","http://inspire.ec.europa.eu/theme/bu"),
            new Atributo(10,"xlink:href","http://inspire.ec.europa.eu/theme/cp"),
            new Atributo(11,"xlink:href","http://inspire.ec.europa.eu/theme/rs"),
            new Atributo(12,"xlink:href","http://inspire.ec.europa.eu/theme/el"),
            new Atributo(13,"xlink:href","http://inspire.ec.europa.eu/theme/er"),
            new Atributo(14,"xlink:href","http://inspire.ec.europa.eu/theme/ef"),
            new Atributo(15,"xlink:href","http://inspire.ec.europa.eu/theme/gg"),
            new Atributo(16,"xlink:href","http://inspire.ec.europa.eu/theme/gn"),
            new Atributo(17,"xlink:href","http://inspire.ec.europa.eu/theme/ge"),
            new Atributo(18,"xlink:href","http://inspire.ec.europa.eu/theme/hb"),
            new Atributo(19,"xlink:href","http://inspire.ec.europa.eu/theme/hh"),
            new Atributo(20,"xlink:href","http://inspire.ec.europa.eu/theme/hy"),
            new Atributo(21,"xlink:href","http://inspire.ec.europa.eu/theme/lc"),
            new Atributo(22,"xlink:href","http://inspire.ec.europa.eu/theme/lu"),
            new Atributo(23,"xlink:href","http://inspire.ec.europa.eu/theme/mf"),
            new Atributo(24,"xlink:href","http://inspire.ec.europa.eu/theme/mr"),
            new Atributo(25,"xlink:href","http://inspire.ec.europa.eu/theme/nz"),
            new Atributo(26,"xlink:href","http://inspire.ec.europa.eu/theme/of"),
            new Atributo(27,"xlink:href","http://inspire.ec.europa.eu/theme/oi"),
            new Atributo(28,"xlink:href","http://inspire.ec.europa.eu/theme/pd"),
            new Atributo(29,"xlink:href","http://inspire.ec.europa.eu/theme/pf"),
            new Atributo(30,"xlink:href","http://inspire.ec.europa.eu/theme/ps"),
            new Atributo(31,"xlink:href","http://inspire.ec.europa.eu/theme/sr"),
            new Atributo(32,"xlink:href","http://inspire.ec.europa.eu/theme/so"),
            new Atributo(33,"xlink:href","http://inspire.ec.europa.eu/theme/sd"),
            new Atributo(34,"xlink:href","http://inspire.ec.europa.eu/theme/su"),
            new Atributo(35,"xlink:href","http://inspire.ec.europa.eu/theme/tn"),
            new Atributo(36,"xlink:href","http://inspire.ec.europa.eu/theme/us")

    );

    private class TextoConAtributo{
        private int idAtributo;
        private String texto;
        public TextoConAtributo(String n,int id){
            this.idAtributo = id;
            texto = n;
        }
        public int getIdAtributo() {
            return idAtributo;
        }
        public String getTexto() {
            return texto;
        }
    }

    private List<TextoConAtributo> TextoConAtributoList = Arrays.asList(
            //new TextoConAtributo("Nacional",1),
            //new TextoConAtributo("Regional",2),
            new TextoConAtributo("Addresses",1),
            new TextoConAtributo("Direcciones",1),
            new TextoConAtributo("Administrative units",1),
            new TextoConAtributo("Unidades administrativas",1),
            new TextoConAtributo("Agricultural and aquaculture facilities",1),
            new TextoConAtributo("Instalaciones agrícolas y de acuicultura",1),
            new TextoConAtributo("Area management/restriction/regulation zones and reporting units",1),
            new TextoConAtributo("Zonas sujetas a ordenación, a restricciones o reglamentaciones y unidades de notificación",1),
            new TextoConAtributo("Atmospheric conditions",1),
            new TextoConAtributo("Condiciones atmosféricas",1),
            new TextoConAtributo("Bio-geographical regions",1),
            new TextoConAtributo("Regiones biogeográficas",1),
            new TextoConAtributo("Buildings",1),
            new TextoConAtributo("Edificios",1),
            new TextoConAtributo("Cadastral parcels",1),
            new TextoConAtributo("Parcelas catastrales",1),
            new TextoConAtributo("Coordinate reference systems",1),
            new TextoConAtributo("Sistemas de coordenadas de referencia",1),
            new TextoConAtributo("Elevation",1),
            new TextoConAtributo("Elevaciones",1),
            new TextoConAtributo("Energy resources",1),
            new TextoConAtributo("Recursos energéticos",1),
            new TextoConAtributo("Environmental monitoring facilities",1),
            new TextoConAtributo("Instalaciones de observación del medio ambiente",1),
            new TextoConAtributo("Geographical grid systems",1),
            new TextoConAtributo("Sistema de cuadrículas geográficas",1),
            new TextoConAtributo("Geographical names",1),
            new TextoConAtributo("Nombres geográficos",1),
            new TextoConAtributo("Geology",1),
            new TextoConAtributo("Geología",1),
            new TextoConAtributo("Habitats and biotopes",1),
            new TextoConAtributo("Hábitats y biotopos",1),
            new TextoConAtributo("Human health and safety",1),
            new TextoConAtributo("Salud y seguridad humanas",1),
            new TextoConAtributo("Hydrography",1),
            new TextoConAtributo("Hidrografía",1),
            new TextoConAtributo("Land cover",1),
            new TextoConAtributo("Cubierta terrestre",1),
            new TextoConAtributo("Land use",1),
            new TextoConAtributo("Uso del suelo",1),
            new TextoConAtributo("Meteorological geographical features",1),
            new TextoConAtributo("Aspectos geográficos de carácter meteorológico",1),
            new TextoConAtributo("Mineral resources",1),
            new TextoConAtributo("Recursos minerales",1),
            new TextoConAtributo("Natural risk zones",1),
            new TextoConAtributo("Zonas de riesgos naturales",1),
            new TextoConAtributo("Oceanographic geographical features",1),
            new TextoConAtributo("Rasgos geográficos oceanográficos",1),
            new TextoConAtributo("Orthoimagery",1),
            new TextoConAtributo("Ortoimágenes",1),
            new TextoConAtributo("Population distribution — demography",1),
            new TextoConAtributo("Distribución de la población — demografía",1),
            new TextoConAtributo("Production and industrial facilities",1),
            new TextoConAtributo("Instalaciones de producción e industriales",1),
            new TextoConAtributo("Protected sites",1),
            new TextoConAtributo("Lugares protegidos",1),
            new TextoConAtributo("Sea regions",1),
            new TextoConAtributo("Regiones marinas",1),
            new TextoConAtributo("Soil",1),
            new TextoConAtributo("Suelo",1),
            new TextoConAtributo("Species distribution",1),
            new TextoConAtributo("Distribución de las especies",1),
            new TextoConAtributo("Statistical units",1),
            new TextoConAtributo("Unidades estadísticas",1),
            new TextoConAtributo("Transport networks",1),
            new TextoConAtributo("Redes de transporte",1),
            new TextoConAtributo("Utility and governmental services",1),
            new TextoConAtributo("Servicios de utilidad pública y estatales",1)
    );
    //////////////////////////////////////////////////////////
}
