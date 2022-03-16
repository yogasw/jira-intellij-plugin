package com.intellij.jira.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.hover.TreeHoverListener;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseEvent;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class SearcherTree extends Tree {

    private final Project myProject;
    private final SearcherTreeModel myModel;

    public SearcherTree(Project project) {
        super();

        myProject = project;
        myModel = new SearcherTreeModel(project);
        setModel(myModel);

        new MyDoubleClickListener().installOn(this);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new MyColoredTreeCellRenderer());
        TreeHoverListener.DEFAULT.addTo(this);
        expandAll();
    }

    @Nullable
    public SearcherTreeNode getSelectedNode() {
        SearcherTreeNode[] nodes = getSelectedNodes(SearcherTreeNode.class, null);
        if (nodes.length == 1) {
            return nodes[0];
        }

        return null;
    }

    @Override
    public SearcherTreeModel getModel() {
        return myModel;
    }

    public void update() {
        getModel().rebuild();

        revalidate();
        repaint();
        expandAll();
    }

    public void expandAll() {
        for (var i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    private class MyColoredTreeCellRenderer extends ColoredTreeCellRenderer {

        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (value == null) {
                return;
            }

            SearcherTreeNode node = (SearcherTreeNode) value;
            if (leaf && nonNull(node.getSearcher())) {
                JQLSearcher selectedSearcher = JQLSearcherManager.getInstance().getSelectedSearcher(myProject);
                if (selectedSearcher.getId().equals(node.getSearcher().getId())) {
                   setIcon(AllIcons.Debugger.Db_no_suspend_breakpoint);
                }

                append(node.getText(), node.isEditable() ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.GRAY_ATTRIBUTES);
                append(" ~ " + node.getSearcher().getJql(), SimpleTextAttributes.GRAY_ATTRIBUTES);
            } else {
                append(node.getText(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
            }

        }
    }

    private class MyDoubleClickListener extends DoubleClickListener {

        @Override
        protected boolean onDoubleClick(@NotNull MouseEvent e) {
            TreePath clickPath = getClosestPathForLocation(e.getX(), e.getY());
            if (clickPath == null) {
                return false;
            }

            TreePath selectionPath = getSelectionPath();
            if (selectionPath == null || clickPath != selectionPath) {
                return false;
            }

            if (!(selectionPath.getLastPathComponent() instanceof SearcherTreeNode)) {
                return false;
            }

            JQLSearcher searcher = ((SearcherTreeNode) selectionPath.getLastPathComponent()).getSearcher();
            if (Objects.isNull(searcher)) {
                return false;
            }

            JQLSearcherManager.getInstance().setSelected(myProject, searcher);

            return true;
        }
    }

}
