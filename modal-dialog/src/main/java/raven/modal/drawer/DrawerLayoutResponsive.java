package raven.modal.drawer;

import com.formdev.flatlaf.util.UIScale;
import raven.modal.component.ModalContainer;
import raven.modal.drawer.menu.MenuOption;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.SimpleDrawerLayoutOption;
import raven.modal.layout.OptionLayoutUtils;

import java.awt.*;

/**
 * This class for responsive, show and hide drawer
 *
 * @author Raven
 */
public class DrawerLayoutResponsive {

    public void setModalContainer(ModalContainer modalContainer) {
        this.modalContainer = modalContainer;
    }

    public ModalContainer getModalContainer() {
        return modalContainer;
    }

    public DrawerPanel getDrawerPanel() {
        return drawerPanel;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
        if (drawerPanel != null) {
            drawerPanel.setVisible(showing);
        }
        drawerPanel.revalidate();
    }

    private ModalContainer modalContainer;
    private DrawerPanel drawerPanel;
    private boolean opened = true;
    private boolean showing = true;

    public DrawerLayoutResponsive(ModalContainer modalContainer, DrawerPanel drawerPanel) {
        this.modalContainer = modalContainer;
        this.drawerPanel = drawerPanel;
    }

    public boolean check(Container container, int width) {
        DrawerBuilder drawerBuilder = drawerPanel.getDrawerBuilder();
        int drawerOpenAt = drawerBuilder.getOpenDrawerAt();
        boolean isOpen = width <= (drawerBuilder.openDrawerAtScale() ? UIScale.scale(drawerOpenAt) : drawerOpenAt) || isUnsupportedCompactMenu(drawerBuilder);
        if (isOpen != opened) {
            // change layout
            if (isOpen) {
                container.remove(drawerPanel);
                if (modalContainer != null) {
                    if (modalContainer.getController() != null) {
                        modalContainer.getController().addModal();
                    }
                    modalContainer.setVisible(true);
                    modalContainer.checkLayerVisible();
                }
            } else {
                if (modalContainer != null) {
                    if (modalContainer.getController() != null) {
                        modalContainer.getController().removeModal();
                    }
                    modalContainer.setVisible(false);
                    modalContainer.checkLayerVisible();
                }
                drawerPanel.checkThemesChanged();
                container.add(drawerPanel);
                drawerPanel.setVisible(showing);
            }
            opened = isOpen;
            drawerOpenChanged(!opened);
        }
        return opened;
    }

    private boolean isUnsupportedCompactMenu(DrawerBuilder drawerBuilder) {
        if (drawerBuilder instanceof SimpleDrawerBuilder) {
            if (((SimpleDrawerBuilder) drawerBuilder).getSimpleMenuOption().getMenuOpenMode() == MenuOption.MenuOpenMode.COMPACT) {
                if (isHorizontalDrawer() == false) {
                    return true;
                }
            }
        }
        return false;
    }

    private void drawerOpenChanged(boolean isOpen) {
        drawerPanel.getDrawerBuilder().drawerOpenChanged(isOpen);
    }

    public Rectangle getDrawerLayout(Container parent) {
        return OptionLayoutUtils.getLayoutLocation(parent, drawerPanel, 1f, drawerPanel.getDrawerBuilder().getOption().getLayoutOption());
    }

    public boolean isHorizontalDrawer() {
        SimpleDrawerLayoutOption layoutOption = (SimpleDrawerLayoutOption) drawerPanel.getDrawerBuilder().getOption().getLayoutOption();
        boolean isHorizontal = layoutOption.getFullSize().getY().floatValue() == 1f;
        return isHorizontal;
    }

    public void revalidateDrawer() {
        if (drawerPanel != null) {
            drawerPanel.setVisible(showing);
        }
    }
}
