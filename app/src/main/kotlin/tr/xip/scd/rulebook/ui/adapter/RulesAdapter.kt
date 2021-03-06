package tr.xip.scd.rulebook.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_rule.view.*
import tr.xip.scd.rulebook.R
import tr.xip.scd.rulebook.data.DataManager
import tr.xip.scd.rulebook.model.Rule

class RulesAdapter(val recycler: RecyclerView, val dataset: List<Rule>, val clickListener: OnItemClickListener?) : RecyclerView.Adapter<RulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent, false)
        if (clickListener != null) {
            v.setOnClickListener {
                clickListener.onClick(dataset[recycler.getChildLayoutPosition(v)])
            }
        }
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.view.context
        val item = dataset[position]

        holder.view.ruleId.text = "${item.id}"
        holder.view.charge.setBackgroundColor(
                if (item.result.contains("+")) {
                    context.resources.getColor(R.color.charge_positive)
                } else {
                    context.resources.getColor(R.color.charge_negative)
                }
        )

        holder.view.rule.text = if (DataManager.getLang() == DataManager.LANG_EN) item.english else item.bosnian
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun onClick(rule: Rule)
    }
}