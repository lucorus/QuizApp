package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Question;
import com.example.myapplication.databinding.ItemQuestionBinding;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.VH> {
    private List<Question> data;
    private final AppDatabase db;

    public QuestionAdapter(AppDatabase db, List<Question> data) {
        this.db = db;
        this.data = data;
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemQuestionBinding b;
        VH(ItemQuestionBinding b) { super(b.getRoot()); this.b = b; }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemQuestionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Question q = data.get(position);
        holder.b.tvQ.setText(q.text);
        holder.b.tvA.setText("Ответ: " + q.answer);
        holder.b.btnDelete.setOnClickListener(v -> {
            db.questionDao().delete(q);
            data.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }
}
