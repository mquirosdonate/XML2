////
//// https://www.w3.org/2003/01/dom2-javadoc/org/w3c/dom/Node.html
////
package xmltools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ParseXML {
    String mFileXML;
    public Document documento = null;
    static  StringBuilder xml = new StringBuilder();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private ReglasXML r = null;
    int mBlancos = 0;
    
    public ParseXML(String xmlfile){
        r = new ReglasXML();
        xml.setLength(0);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        mFileXML = xmlfile;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            documento = builder.parse(new File( xmlfile ));
        }catch (Exception e){    }

        //Normalizar la Estructura del XML
        //documento.getDocumentElement().normalize();
    }
    String VALOR_ATTR="",VALOR1="",VALOR2="",VALOR3="";
    private void recorreNodosHijos(Node node)  {
        mBlancos += 2;
        int blancos = mBlancos;
        NodeList listaDeNodos = node.getChildNodes();
        for (int i = 0; i < listaDeNodos.getLength(); i++) {
            Node nodo = listaDeNodos.item(i);
            if (nodo.getNodeType() != Node.ELEMENT_NODE) continue;
            if (r.eliminaNodo(nodo)) continue;
            boolean tieneHijos = tieneHijosTipoElementNode(nodo);
             xml.append(tabulación(blancos));
             String TAG = getTAGyAtributos(nodo);
             String texto = getTextoDelNodo(nodo)
                     .replace("&","&amp;")
                     .replace("<","&lt;")
                     .replace(">","&gt;")
                     .replace("\"","&quot;")
                     .replace("\'","&apos;");
             //////////////////////////////////////
            if (!VALOR1.isEmpty() && TAG.compareTo("AccessConstraints")==0)
                texto = VALOR1;

             if (!VALOR2.isEmpty() && TAG.compareTo("Identifier")==0)
                 texto = VALOR2;

            if (!VALOR3.isEmpty() && TAG.compareTo("Fees")==0)
                texto = VALOR3;
            //////////////////////////////////////
             if (tieneHijos)  recorreNodosHijos(nodo);
             xml.append(texto);
             if (tieneHijos)  xml.append(tabulación(blancos));
             xml.append("</" + TAG + ">");
            }
        mBlancos -= 2;
    }
    private String getTAGyAtributos(Node nodo){
        String TAG = nodo.getNodeName();
        r.addAtributo(nodo);
        xml.append("<"+TAG);
        if (nodo.hasAttributes()) {
            NamedNodeMap nodoMap = nodo.getAttributes();
            for (int i = 0; i < nodoMap.getLength(); i++) {
                Node tempNodo = nodoMap.item(i);
                String nombreAtributo = tempNodo.getNodeName();
                String valorAtributo = tempNodo.getNodeValue()
                        .replace("&","&amp;")
                        .replace("<","&lt;")
                        .replace(">","&gt;")
                        .replace("\"","&quot;")
                        .replace("'","&apos;");
                //ELIMINAMOS UN ATRIBUTO DEL NODO RAIZ
                if (nombreAtributo.compareTo("xmlns:esri_wms") == 0 &&
                        valorAtributo.compareTo("http://www.esri.com/wms") == 0)
                    continue;

                if (!VALOR_ATTR.isEmpty() && TAG.compareTo("AuthorityURL")==0  && nombreAtributo.compareTo("name")==0){
                    valorAtributo = VALOR_ATTR;
                }
                if (!VALOR_ATTR.isEmpty() && TAG.compareTo("Identifier")==0  && nombreAtributo.compareTo("authority")==0){
                    valorAtributo = VALOR_ATTR;
                }
                //////////////////////////////////////////
                xml.append(" " +nombreAtributo+ "=\"" + valorAtributo+"\"");
            }
        }
        xml.append(">");
        return TAG;
    }

    ////////////////////////////////////////////////////
    private  List<Node> nodeList = new ArrayList<>();
    public  List<Node> getNodos(String path,Document doc){
        nodeList.clear();
        Element root = doc.getDocumentElement();
        NodeList listaDeNodos = doc.getElementsByTagName(root.getNodeName());
        Node node = listaDeNodos.item(0);
        getHijos(node,path);
        return nodeList;
    }
    private  void getHijos(Node node,String path)  {
        NodeList listaDeNodos = node.getChildNodes();
        for (int i = 0; i < listaDeNodos.getLength(); i++) {
            Node nodo = listaDeNodos.item(i);

            if (nodo.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String mi_path = getPathDelNodo(nodo);
            if (!path.contains(mi_path))
                continue;
            if (mi_path.equals(path)) {
                nodeList.add(nodo);
                return ;
            }
            if (tieneHijosTipoElementNode(nodo))
                getHijos(nodo,path);
        }
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
    private String getTextoDelNodo(Node node) {
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
    private static boolean tieneHijosTipoElementNode(Node node){
        NodeList nodeChilds = node.getChildNodes();
        for (int i = 0; i < nodeChilds.getLength();i++) {
            Node nodeChild = nodeChilds.item(i);
            if (nodeChild.getNodeType() == Node.ELEMENT_NODE ) return true;
        }
        return false;
    }
    private String tabulación(int nBlancos){
        String format = "\n%1$" + nBlancos + "s";
        return String.format(format," ");
    }
    //////////////////////////////////////////////////////////

    // Métodos públiclos de la clase
    public static Document loadXMLFromString(String xml) throws Exception  {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    String[] links = {"http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/noLimitations",
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1a" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1b" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1c" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1d" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1d" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1f" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1g" ,
            "http://inspire.ec.europa.eu/metadata-codelist/LimitationsOnPublicAccess/INSPIRE_Directive_Article13_1h"};
    private boolean compareLink(String link){
        for(String l:links){
            if (l.compareTo(link)==0)
                return true;
        }
        return false;
    }
    public void recorreDOM(String auxXML){
        if (!auxXML.isEmpty()){
            try {
                Document my_xmlAux = loadXMLFromString(auxXML);
                my_xmlAux.getDocumentElement().normalize();
                String path = "csw:GetRecordByIdResponse/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:RS_Identifier/gmd:codeSpace/gco:CharacterString";
                List<Node> mis_nodos = getNodos(path,my_xmlAux);
                if (mis_nodos.size()>0){
                    for (Node nodo: mis_nodos){
                        VALOR_ATTR = getTextoDelNodo(nodo);
                        break;
                    }
                }
                path = "csw:GetRecordByIdResponse/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:RS_Identifier/gmd:code/gco:CharacterString";
                 mis_nodos = getNodos(path,my_xmlAux);
                if (mis_nodos.size()>0){
                    for (Node nodo: mis_nodos){
                        VALOR2 = getTextoDelNodo(nodo);
                        break;
                    }
                }
                VALOR3 = "";
                VALOR1 = "";
                path = "csw:GetRecordByIdResponse/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor";
                mis_nodos = getNodos(path,my_xmlAux);
                if (mis_nodos.size()>0){
                    for (Node nodo: mis_nodos){
                        if (nodo.hasAttributes()) {
                            NamedNodeMap nodoMap = nodo.getAttributes();
                            for (int i = 0; i < nodoMap.getLength(); i++) {
                                String nombreAtributo = nodoMap.item(i).getNodeName();
                                String valorAtributo = nodoMap.item(i).getNodeValue();
                                if  (nombreAtributo.compareTo("xlink:href")==0){
                                    if (compareLink(valorAtributo)){
                                        VALOR1 += getTextoDelNodo(nodo)+".";
                                    }else{
                                        VALOR3 += getTextoDelNodo(nodo)+".";
                                    }
                                }
                            }
                        }
                    }
                }
                path = "csw:GetRecordByIdResponse/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString";
                mis_nodos = getNodos(path,my_xmlAux);
                if (mis_nodos.size()>0){
                    for (Node nodo: mis_nodos){
                        VALOR3 += getTextoDelNodo(nodo)+".";
                        break;
                    }
                }
            }catch (Exception e){    }
        }

        Element root = documento.getDocumentElement();
        NodeList listaDeNodos = documento.getElementsByTagName(root.getNodeName());
        Node nodo = listaDeNodos.item(0);

        String TAG = getTAGyAtributos(nodo);
        recorreNodosHijos(nodo);
        xml.append("\n</"+TAG+">");
    }
    public String getXML(){
        return xml.toString();
    }
    public  String writeXML(){
        String salida = xml.toString().
                replace("AM.FloodUnitOfManagement.Default.Polygon","AM.FloodUnitOfManagement.Default").
                replace("AM.FloodUnitOfManagement.Default.Line","AM.FloodUnitOfManagement.Default").
                replace("AM.NitrateVulnerableZone.Default.Polygon","AM.NitrateVulnerableZone.Default").
                replace("AM.RiverBasinDistrict.Default.Polygon","AM.RiverBasinDistrict.Default").
                replace("AM.SensitiveArea.Default.Polygon","AM.SensitiveArea.Default").
                replace("AM.SensitiveArea.Default.Line","AM.SensitiveArea.Default").
                replace("AM.SensitiveArea.Default.Point","AM.SensitiveArea.Default").
                replace("AM.WaterBodyForWFD.Default.Point","AM.WaterBodyForWFD.Default").
                replace("AM.WaterBodyForWFD.Default.Polygon","AM.WaterBodyForWFD.Default").
                replace("AM.WaterBodyForWFD.Default.Line","AM.WaterBodyForWFD.Default").
                replace("EF.EnvironmentalMonitoringFacilities.Default.Point","EF.EnvironmentalMonitoringFacilities.Default").
                replace("EF.EnvironmentalMonitoringFacilities.Default.Polygon","EF.EnvironmentalMonitoringFacilities.Default").
                replace("GE.GroundWaterBody.Default.Polygon","GE.GroundWaterBody").
                replace("HH.HealthStatisticalData.RiesgoPob.Polygon","HH.HealthStatisticalData.Default").
                replace("HY.PhysicalWaters.Catchments.Default.RiverBasin","HY.PhysicalWaters.Catchments.Default").
                replace("HY.PhysicalWaters.Catchments.Default.DrainageBasin","HY.PhysicalWaters.Catchments.Default").
                replace("HY.PhysicalWaters.ManMadeObject.Default.DamOrWeir","HY.PhysicalWaters.ManMadeObject.Default").
                replace("HY.PhysicalWaters.ManMadeObject.Default.bridge","HY.PhysicalWaters.ManMadeObject.Default").
                replace("HY.PhysicalWaters.Waterbodies.Default.Polygon","HY.PhysicalWaters.Waterbodies.Default").
                replace("HY.PhysicalWaters.Waterbodies.Default.Polyline","HY.PhysicalWaters.Waterbodies.Default").
                replace("HY.PhysicalWaters.Waterbodies.Man.Made.ShoreLineConstruction","HY.PhysicalWaters.ManMadeObject.Default").
                replace("NZ.ExposedElement.Default.Point","NZ.ExposedElement").
                replace("NZ.ExposedElement.Default.Polygon","NZ.ExposedElement").
                replace("NZ.RiskZone.RiesgoAct","NZ.RiskZone").
                replace("NZ.RiskZone.RiesgoPob","NZ.RiskZone").
                replace("NZ.RiskZone.ARPSIS.Line","NZ.RiskZone").
                replace("NZ.RiskZone.Default.Q100","NZ.RiskZone").
                replace("NZ.RiskZone.Default.Q50","NZ.RiskZone").
                replace("NZ.RiskZone.Default.Q500","NZ.RiskZone").
                replace("NZ.RiskZone.Default.NormasExplotacion","NZ.RiskZone").
                replace("PS.ProtectedSite.Default.Polygon","PS.ProtectedSite.Default").
                replace("PS.ProtectedSite.Default.Point","PS.ProtectedSite.Default").
                replace("PS.ProtectedSite.Default.Line","PS.ProtectedSite.Default").
                replace("US.EnvironmentalManagementInstallation.Default.Point","US.EnvironmentalManagementInstallation.Default").
                replace("EF.EnvironmentalMonitoringNetworks.Default.Point","EF.EnvironmentalMonitoringNetwork.Default").
                replace("BU.Building.Default.Point","BU.Building.default").
                replace("PF.ProductionFacility.Default.Polygon","PF.ProductionFacility").
                replace("PF.ProductionSite.Default.Polygon","PF.ProductionSite").
                replace("AM.ForestManagementArea.Default.Polygon","AM.ForestManagementArea.Default").
                replace("BR.Bio-geographicalRegion.Natura2000AndEmerald.Default.Polygon","BR.Bio-geographicalRegion.Natura2000AndEmerald").
                replace("NZ.HazardArea.ErosionEolica","NZ.HazardArea").
                replace("NZ.HazardArea.Erosion","NZ.HazardArea").
                replace("NZ.HazardArea.MovimientosMasa","NZ.HazardArea").
                replace("NZ.HazardArea.ErosionCauces","NZ.HazardArea").
                replace("NZ.ObservedEvent.Default.Polygon","NZ.ObservedEvent").
                replace("AM.CoastalZoneManagementArea.Default.Line","AM.CoastalZoneManagementArea.Default").
                replace("AM.CoastalZoneManagementArea.Default.Polygon","AM.CoastalZoneManagementArea.Default").
                replace("EF.EnvironmentalMonitoringFacilities.Default.Curve","EF.EnvironmentalMonitoringFacilities.Default").
                replace("EF.EnvironmentalMonitoringFacilities.Default.point","EF.EnvironmentalMonitoringFacilities.Default").
                replace("AM.MarineRegion.Default.Polygon","AM.MarineRegion.Default").
                replace("LU.ExistingLandUse.Default.Polygon","LandUse.ExistingLandUse.Default").
                replace("AF.Site.Default.Point","AF.Site").
                replace("AF.Site.Default.Polygon","AF.Site").
                replace("AU.AdministrativeUnit.Default.Polygon","AU.AdministrativeUnit.Default").
                replace("AM.AirQualityManagementZone.Default.Polygon","AM.AirQualityManagementZone.Default").
                replace("AM.NoiseRestrictionZone.Default.Line","AM.NoiseRestrictionZone.Default").
                replace("AM.NoiseRestrictionZone.Default.Polygon","AM.NoiseRestrictionZone.Default").
                replace("AF.AquacultureHolding.Default.Point","AF.AquacultureHolding");

        OutputStreamWriter fout = null;
        String path = "";
        String nameFile = "";

        try {
            File file = new File(mFileXML);
            path = file.getParent();
            nameFile = file.getName();
            nameFile = "_" + nameFile;
            file = new File(path,nameFile);
            fout = new OutputStreamWriter(new FileOutputStream(file, false));
            fout.write(salida);
            fout.flush();
            fout.close();
        }catch (Exception e){return "???";}
        return path + "/" +nameFile;
    }
}
