<?php 
	echo $this->Html->script('bbcode', array('inline' => false));
	echo $this->Html->css('bbcode', null, array('inline' => false));
?>

<div id="show_preview">	
</div>

<div id="box" style="background-color:#7C6751;">
	<div id="toolbar" style="float:left;">
		<img class="button" id="bold" src="/img/bbcode/bold.gif" title="Bold" onclick="doAddTags('[b]','[/b]','text')">
		<img class="button" id="italic" src="/img/bbcode/italic.gif" title="Italic" onclick="doAddTags('[i]','[/i]','text')">
		<img class="button" id="underline" src="/img/bbcode/underline.gif" title="Underline" onclick="doAddTags('[u]','[/u]','text')">
		<img class="button" id="strikeout" src="/img/bbcode/strikeout.gif" title="Strike out" onclick="doAddTags('[s]','[/s]','text')">
		<img class="button" id="center" src="/img/bbcode/center.gif" title="Center" onclick="doAddTags('[center]','[/center]','text')">
		<img class="button" id="link" src="/img/bbcode/link.gif" title="Insert URL Link" onclick="doURL('text')">
		<img class="button" id="picture" src="/img/bbcode/picture.gif" title="Insert Image" onclick="doImage('text')">
		<img class="button" id="ordered" src="/img/bbcode/ordered.gif" title="Ordered List" onclick="doList('[ol]','[/ol]','text')">
		<img class="button" id="unordered" src="/img/bbcode/unordered.gif" title="Unordered List" onclick="doList('[ul]','[/ul]','text')">
		<img class="button" id="quote" src="/img/bbcode/quote.gif" title="Quote" onclick="doAddTags('[quote]','[/quote]','text')">
		<img class="button" id="code" src="/img/bbcode/code.gif" title="Code" onclick="doAddTags('[code]','[/code]','text')">
	</div>
	<div id="margin">
		<select id="size" onchange="doAddTags('[size=' + this.value + ']','[/size]','text')">
			<option value="standard" disabled="disabled" selected="selected">Size</option>
			<option value="6">Tiny</option>
			<option value="8">Small</option>
			<option value="14">Large</option>
			<option value="18">Huge</option>
		</select>&nbsp;
		<select id="color" onchange="doAddTags('[color=' + this.value + ']','[/color]','text')">
			<option value="standard"  disabled="disabled" selected="selected">Color</option>
			<option value="red">Red</option>
			<option value="blue">Blue</option>
			<option value="LimeGreen">Green</option>
			<option value="yellow">Yellow</option>
			<option value="black">Black</option>
		</select>&nbsp;
		Select text first to apply effect directly on it.
	
		<button id="BBpreview" onclick="preview('text', '<?php echo $destination ?>')">Preview</button>		
		<span class="submitArea">
			&nbsp;<button id="BBsubmit" onclick="submit('text', '<?php echo $destination ?>', '<?php echo @$redirect ?>')">Submit</button>
		</span>
	</div>
</div>

<?php
	echo $this->Form->textarea('text', array(
		'id' => 'text',
		'style' => $style,
		'value' => @$value
	));
?>