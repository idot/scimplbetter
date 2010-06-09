package at.idot.scimplbetter.lib

import net.liftweb.widgets.tablesorter.TableSorter
import net.liftweb.widgets.tablesorter.TableSorter
/*
 * Copyright 2007-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import net.liftweb.http.ResourceServer
import scala.xml.NodeSeq
import net.liftweb.http.{LiftRules}
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JE
import net.liftweb.http.js.jquery.JqJE
import net.liftweb.widgets.tablesorter.TableSorter
/***
* Attaches JQuery TableSorter plugin [[http://tablesorter.com/docs/]] to a table.
* 
* Usage: 
* a. initialize TableSorter widget it Boot: TableSorter.init
* b. create a table with {{{<lift:TableSnippet.render/>  <table id="theselector"><thead></thead> and <tbody></tbody></table> }}} in your template
* c. in your rendering function call {{{ TableSorter("#theselector") or TableSorter("#theselector","#thepager") }}}
* d. To enable paging add a pager to the table
*  
* {{{ 
*  <div id="thepager" class="pager">
*                <img src="/classpath/tablesorter/addons/pager/icons/first.png" class="first"/>
*                <img src="/classpath/tablesorter/addons/pager/icons/prev.png" class="prev"/>
*                <input type="text" class="pagedisplay" readonly="readonly" size="5" name="page" style="background-color:#FFFFFF;border-style:solid"/>
*                <img src="/classpath/tablesorter/addons/pager/icons/next.png" class="next"/>
*                <img src="/classpath/tablesorter/addons/pager/icons/last.png" class="last"/>
*                <select class="pagesize">
*                        <option selected="selected"  value="10">10</option>
*                        <option value="20">20</option>
*                        <option value="30">30</option>
*                        <option  value="40">40</option>
*                </select>
*  </div>
*  }}}
*/
object PagedTableSorter {
  /***
  *  Attaches the TableSorter to a selector (no paging)
  */
  def apply(selector: String) = renderOnLoad(selector, "")

  /***
  *  Attaches a Tablesorter to a selector and pages (when pages != "")
  */
  def apply(selector: String, pager: String) = renderOnLoad(selector, pager)


  def renderOnLoad(selector: String,pager: String) = {
    val onLoad ="""jQuery(function($){
            $('"""+selector+"""')."""+sorterCommand(pager)+""";
            });
            """
    <head>
      <link rel="stylesheet" href={"/" + LiftRules.resourceServerPath + "/tablesorter/themes/blue/style.css"} type="text/css" id="" media="print, projection, screen" />
      <script type="text/javascript" src={"/" + LiftRules.resourceServerPath + "/tablesorter/jquery.tablesorter.js"}></script>
      {if(pager != ""){<script type="text/javascript" src={"/" + LiftRules.resourceServerPath + "/tablesorter/addons/pager/jquery.tablesorter.pager.js"}></script> }else{ NodeSeq.Empty }}
      <script type="text/javascript" charset="utf-8">{onLoad}</script>
    </head>
  }

  /**
  * creates parts of the javascript sorter command
  * @param pager: the pager class to attach to or empty string for no pager
  *
  */
  def sorterCommand(pager: String) = {
      "tablesorter({sortList:[[0,0]], widgets:['zebra']})" + (if(pager != "") ".tablesorterPager({container: $('"+pager+"')})" else "")
  }

}




