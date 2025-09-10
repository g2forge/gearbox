package com.g2forge.gearbox.issue.document;

import com.g2forge.enigma.document.model.Block;
import com.g2forge.enigma.document.model.Block.BlockBuilder;
import com.g2forge.enigma.document.model.DocList;
import com.g2forge.enigma.document.model.DocList.DocListBuilder;
import com.g2forge.enigma.document.model.DocList.Marker;
import com.g2forge.enigma.document.model.IBlock;
import com.g2forge.enigma.document.model.Text;
import com.g2forge.gearbox.issue.IEnumIssueType;
import com.g2forge.gearbox.issue.Level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentIssueType implements IDocumentIssueType<DocumentPayload>, IEnumIssueType<DocumentIssueType, DocumentPayload> {
	BasicIssue("This is a basic issue", Level.WARN),
	FancyIssue("This is a fancy issue", Level.INFO) {
		@Override
		public IBlock computeDocument(DocumentPayload payload) {
			final BlockBuilder block = Block.builder().type(Block.Type.Block);
			block.content(computeSummary(payload));

			final DocListBuilder list = DocList.builder().marker(Marker.Numbered);
			for (String item : payload.getItems()) {
				list.item(new Text(item));
			}
			block.content(list.build());

			return block.build();
		}
	};

	protected final String description;

	protected final Level level;

	@Override
	public String computeMessage(DocumentPayload payload) {
		return getDescription() + ", with " + payload.getItems().size() + " items";
	}
}
