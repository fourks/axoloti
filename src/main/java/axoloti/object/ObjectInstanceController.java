package axoloti.object;

import axoloti.MainFrame;
import axoloti.PatchController;
import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceController;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import axoloti.mvc.IView;
import axoloti.mvc.array.ArrayController;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<IAxoObjectInstance, IView, PatchController> {

    public static final String OBJ_LOCATION = "Location";
    public static final String OBJ_SELECTED = "Selected";
    public static final String OBJ_INSTANCENAME = "InstanceName";
    public static final String OBJ_PARAMETER_INSTANCES = "ParameterInstances";
    public static final String OBJ_ATTRIBUTE_INSTANCES = "AttributeInstances";
    public static final String OBJ_INLET_INSTANCES = "InletInstances";
    public static final String OBJ_OUTLET_INSTANCES = "OutletInstances";
    public static final String OBJ_DISPLAY_INSTANCES = "DisplayInstances";
    public static final String OBJ_COMMENT = "CommentText";

    @Override
    public String[] getPropertyNames() {
        return new String[]{
            OBJ_LOCATION,
            OBJ_SELECTED,
            OBJ_INSTANCENAME,
            OBJ_PARAMETER_INSTANCES,
            OBJ_ATTRIBUTE_INSTANCES,
            OBJ_INLET_INSTANCES,
            OBJ_OUTLET_INSTANCES,
            OBJ_DISPLAY_INSTANCES};
    }

    public final ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController> attributeInstanceControllers;
    public final ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController> parameterInstanceControllers;
    public final ArrayController<InletInstanceController, InletInstance, ObjectInstanceController> inletInstanceControllers;
    public final ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController> outletInstanceControllers;
    public final ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController> displayInstanceControllers;

    public ObjectInstanceController(IAxoObjectInstance model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);

        attributeInstanceControllers = new ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController>(this, OBJ_ATTRIBUTE_INSTANCES) {

            @Override
            public AttributeInstanceController createController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new AttributeInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AttributeInstanceController controller) {
            }
        };
        parameterInstanceControllers = new ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController>(this, OBJ_PARAMETER_INSTANCES) {

            @Override
            public ParameterInstanceController createController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new ParameterInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(ParameterInstanceController controller) {
            }
        };
        inletInstanceControllers = new ArrayController<InletInstanceController, InletInstance, ObjectInstanceController>(this, OBJ_INLET_INSTANCES) {

            @Override
            public InletInstanceController createController(InletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new InletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(InletInstanceController controller) {
                if (getParent() != null) {
                    getParent().disconnect(controller.getModel());
                }
            }
        };
        outletInstanceControllers = new ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController>(this, OBJ_OUTLET_INSTANCES) {

            @Override
            public OutletInstanceController createController(OutletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new OutletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(OutletInstanceController controller) {
                getParent().disconnect(controller.getModel());
            }
        };
        displayInstanceControllers = new ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController>(this, OBJ_DISPLAY_INSTANCES) {

            @Override
            public DisplayInstanceController createController(DisplayInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new DisplayInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(DisplayInstanceController controller) {
            }
        };
    }

    public void ConvertToEmbeddedObj() {
        /*
        try {
            List<IAxoObject> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/object", null);
            assert (!ol.isEmpty());
            IAxoObject o = ol.get(0);
            String iname = getModel().getInstanceName();
            AxoObjectInstancePatcherObject oi = (AxoObjectInstancePatcherObject) getModel().getPatchModel().ChangeObjectInstanceType1(this, o);
            IAxoObject ao = getModel().getType();
            oi.ao = new AxoObjectPatcherObject(ao.getId(), ao.getDescription());
            oi.ao.copy(ao);
            oi.ao.sPath = "";
            oi.ao.upgradeSha = null;
            oi.ao.CloseEditor();
            oi.setInstanceName(iname);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    public void changeLocation(int x, int y) {
        if ((getModel().getX() != x) || (getModel().getY() != y)) {
            Point p = new Point(x, y);
            setModelUndoableProperty(OBJ_LOCATION, p);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(OBJ_PARAMETER_INSTANCES)) {
            parameterInstanceControllers.syncControllers();
        } else if (propertyName.equals(OBJ_ATTRIBUTE_INSTANCES)) {
            attributeInstanceControllers.syncControllers();
        } else if (propertyName.equals(OBJ_DISPLAY_INSTANCES)) {
            displayInstanceControllers.syncControllers();
        } else if (propertyName.equals(OBJ_INLET_INSTANCES)) {
            inletInstanceControllers.syncControllers();
        } else if (propertyName.equals(OBJ_OUTLET_INSTANCES)) {
            outletInstanceControllers.syncControllers();
        }
        super.propertyChange(evt);
        if (getParent()!=null){
            getParent().checkCoherency();
        }
    }

}
