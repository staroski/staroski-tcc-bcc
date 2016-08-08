<?php
/**
 *  MicroEmulator
 *  Copyright (C) 2006-2008 Vadym Pinchuk
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id: jnlp-opener.php 2555 2008-12-11 07:45:28Z skarzhevskyy $
 */

    header('Content-Type: application/x-java-jnlp-file');

    $appURL = $_GET['app-url'];
    $jadURL = 'http://' . $appURL . '.jad';

    $jnlpFileName = "demo.jnlp";

    $jnlpRewritDir = "tck-dev-2/";

    $jnlpFilePath = "../" . $jnlpFileName;
    $fh = fopen($jnlpFilePath, 'r');
    $xml = fread($fh, filesize($jnlpFilePath));
    fclose($fh);
    $xml = ereg_replace('<!--jadRewrite-->', '<argument>-Xautotest:' . $jadURL . '</argument>', $xml);
    $xml = ereg_replace('<!--propertyRewrite-->', '<property name="bluecove.deviceID" value="2" />', $xml);

    if (strlen($appURL) > 0) {
        $patern_href = 'href="' . $jnlpFileName . '"';
        $new_href = 'href="' . $jnlpRewritDir . $appURL . '.jnlp"';
        $xml = str_replace($patern_href, $new_href, $xml);
    }

    echo($xml);
?>
