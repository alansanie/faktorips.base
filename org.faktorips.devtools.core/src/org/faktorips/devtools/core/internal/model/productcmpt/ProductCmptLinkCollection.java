/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.productcmpt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;

/**
 * Contains all {@link IProductCmptLink link} instances for one {@link IProductCmptLinkContainer
 * product component link container}. While link containers define a common interface for IPS
 * objects that can contain links, this class actually holds and manages the link instances. It is
 * designed to be used by {@link IProductCmptLinkContainer} implementations.
 * <p>
 * Introduced for static associations. Both {@link IProductCmpt product components} and
 * {@link IProductCmptGeneration product component generations} can contain links.
 * <p>
 * Note: this class does not ensure that all link instances are part of the same parent. It accepts
 * all link instance regardless of their parent. It also accepts links with undefined association
 * (e.g. <code>link.getAssociation()</code> returns <code>null</code>)
 * 
 * @since 3.8
 * @author widmaier
 */
public class ProductCmptLinkCollection {

    private List<IProductCmptLink> links = new ArrayList<IProductCmptLink>();

    /**
     * Returns all links in this collection for a given association. Returns an empty list if there
     * are no associations for the given name.
     * 
     * @param associationName the association name whose instances (links) should be returned.
     */
    public List<IProductCmptLink> getLinks(String associationName) {
        List<IProductCmptLink> linksForAssociation = getLinksAsMap().get(associationName);
        if (linksForAssociation == null) {
            return new ArrayList<IProductCmptLink>();
        } else {
            return linksForAssociation;
        }
    }

    /**
     * Returns all links in this collection as a map. The name of the product component type
     * association is used as a key. A list of all links belonging to a specific association is the
     * value. e.g. <code>getLinksAsMap().get("aSpecificAssociation")</code> will return a list of
     * all links that are instances of the association "aSpecificAssociation".
     * <p>
     * Allows links with association <code>null</code>. Thus all all links with undefined
     * association are returned when calling <code>getLinksAsMap().get(null)</code>.
     */
    public Map<String, List<IProductCmptLink>> getLinksAsMap() {
        Map<String, List<IProductCmptLink>> associationNameToLinksMap = new LinkedHashMap<String, List<IProductCmptLink>>();
        for (IProductCmptLink link : links) {
            addToList(associationNameToLinksMap, link);
        }
        return associationNameToLinksMap;
    }

    private void addToList(Map<String, List<IProductCmptLink>> associationNameToLinksMap, IProductCmptLink link) {
        String associationName = link.getAssociation();
        List<IProductCmptLink> linksForAssciation = associationNameToLinksMap.get(associationName);
        if (linksForAssciation == null) {
            linksForAssciation = new ArrayList<IProductCmptLink>();
            associationNameToLinksMap.put(associationName, linksForAssciation);
        }
        linksForAssciation.add(link);
    }

    /**
     * Returns all links in this collection.
     * 
     * 
     * The associations are returned in their natural order. This includes the order in which the
     * associations are defined in the corresponding {@link IProductCmptType} and the order of links
     * for a specific association.
     * <p>
     * e.g. links for the association "standardCoverages" will be returned in front of links for
     * "additionalCoverages" if these associations were defined in the model that way. All links for
     * "additionalCoverages" will be returned in the order they are defined in the product component
     * (but after all "standardCoverages"-links).
     */
    public List<IProductCmptLink> getLinks() {
        List<IProductCmptLink> allLinks = new ArrayList<IProductCmptLink>();
        for (List<IProductCmptLink> linkList : getLinksAsMap().values()) {
            allLinks.addAll(linkList);
        }
        return allLinks;
    }

    /**
     * Adds the given link to this link collection.
     * 
     * @param link the link to be added
     * @return <code>true</code> if the link could be added to this collection, <code>false</code>
     *         otherwise.
     * @throws NullPointerException if the given link is <code>null</code>.
     * @throws IllegalArgumentException if the given link does not belong to any association, IOW
     *             {@link IProductCmptLink#getAssociation()} returns <code>null</code>.
     */
    public boolean addLink(IProductCmptLink link) {
        throwExceptionIfLinkCannotBeAdded(link);
        return addLinkInternal(link);
    }

    private void throwExceptionIfLinkCannotBeAdded(IProductCmptLink link) {
        if (link == null) {
            throw new NullArgumentException("Cannot add \"null\" to a ProductCmptLinkCollection"); //$NON-NLS-1$
        }
    }

    /**
     * Creates (and returns) a new link.
     * 
     * @param container the container the new link should be part of
     * @param association the original association the new link is an instance of
     * @param partId the part id the new link should have
     * @return the newly creates
     */
    public IProductCmptLink newLink(IProductCmptLinkContainer container,
            IProductCmptTypeAssociation association,
            String partId) {
        return null;
    }

    /**
     * Creates (and returns) a new link.
     * 
     * @param container the container the new link should be part of
     * @param associationName the name of the original association the new link is an instance of
     * @param partId the part id the new link should have
     * @return the newly creates
     */
    public IProductCmptLink newLink(IProductCmptLinkContainer container, String associationName, String partId) {
        IProductCmptLink link = createLink(container, associationName, partId);
        addLink(link);
        return link;
    }

    protected IProductCmptLink createLink(IProductCmptLinkContainer container, String associationName, String partId) {
        IProductCmptLink link = new ProductCmptLink(container, partId);
        link.setAssociation(associationName);
        return link;
    }

    private boolean addLinkInternal(IProductCmptLink link) {
        links.add(link);
        return false;
    }

    public void insertLink(IProductCmptLink link, IProductCmptLink insertInFrontOf) {

    }

    public void moveLink(IProductCmptLink toMove, IProductCmptLink target, boolean before) {

    }

    /**
     * Removes the given link from this link collection. Does nothing if the given link is
     * <code>null</code> or if it does not belong to any association, IOW
     * {@link IProductCmptLink#getAssociation()} returns <code>null</code>.
     * 
     * @param link the link to be removed
     * @return <code>true</code> if the link was removed from this collection, <code>false</code>
     *         otherwise.
     */
    public boolean remove(IProductCmptLink link) {
        if (link == null) {
            return false;
        }
        return removeInternal(link);
    }

    private boolean removeInternal(IProductCmptLink link) {
        return links.remove(link);
    }

    /**
     * Removes all links from this collection.
     */
    public void clear() {
        links.clear();
    }

    /**
     * Returns <code>true</code> if the link is contained in this collection, <code>false</code>
     * else.
     * 
     * @param link the link to be searched for in this collection
     */
    public boolean containsLink(IProductCmptLink link) {
        if (link == null) {
            return false;
        }
        return containsLinkInternal(link);
    }

    private boolean containsLinkInternal(IProductCmptLink link) {
        return links.contains(link);
    }

}