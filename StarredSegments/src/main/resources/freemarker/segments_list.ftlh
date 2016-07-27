<#include "html_header.ftlh" />

<form action="" method="post">

    <#include "header.ftlh" />

    <div class="container">
        <#list segments as segment>
            <input id="selected_${segment.id?c}" class="hide" type="checkbox" name="selected" value="${segment.id?c}"
                   checked>
            <label class="rounded active shadow map cell check" for="selected_${segment.id?c}"
                   style="background-size: cover; background-image: url(map.do?segmentId=${segment.id?c});">
                <div class="rounded-top shade">
                    <input name="name_${segment.id?c}" value="${segment.name}" placeholder="${segment.name}"
                           class="strip fill" type="text">
                </div>
            </label>
        </#list>
    </div>

    <div class="footer">
        <div class="container">
            <input class="strip rounded active shadow cell span-2" type="submit" value="Convert to CRS">
        </div>
    </div>

</form>

<#include "html_footer.ftlh" />