/*
 * Copyright 2011 Josh Kropf
 *
 * This file is part of bb-ant-tools.
 *
 * bb-ant-tools is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * bb-ant-tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bb-ant-tools; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package ca.slashdev.bb.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Exists purely for syntactical sugar
 * @author Josh Kropf <josh@slashdev.ca>
 */
public class NodeIterable implements Iterable<Node> {
   public static NodeIterable fromChildren(Node node) {
      return new NodeIterable(node.getChildNodes());
   }

   private NodeList _list;
   private final int _length;

   public NodeIterable(NodeList list) {
      _list = list;
      _length = _list.getLength();
   }

   @Override
   public Iterator<Node> iterator() {
      return new Iterator<Node>() {
         private int _cursor;

         @Override
         public boolean hasNext() {
            return _cursor < _length;
         }

         @Override
         public Node next() {
            if (!hasNext()) {
               throw new NoSuchElementException();
            }

            return _list.item(_cursor++);
         }

         @Override
         public void remove() { }
      };
   }
}
